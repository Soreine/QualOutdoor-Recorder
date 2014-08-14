package com.qualoutdoor.recorder.recording;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.qualoutdoor.recorder.GlobalConstants;
import com.qualoutdoor.recorder.R;
import com.qualoutdoor.recorder.network.DataSendingManager;
import com.qualoutdoor.recorder.network.SendCompleteListener;
import com.qualoutdoor.recorder.persistent.CollectMeasureException;
import com.qualoutdoor.recorder.persistent.DataBaseException;
import com.qualoutdoor.recorder.persistent.FileGenerator;
import com.qualoutdoor.recorder.persistent.FileReadyListener;
import com.qualoutdoor.recorder.persistent.SQLConnector;
import com.qualoutdoor.recorder.persistent.Sample;

/** This Handler is used to manage AsyncTasks related to the database */
public class RecordingHandler extends Handler {

    /** Message code for starting recording */
    public static final int MESSAGE_START_RECORD = 101;
    /** Message code for stoping recording */
    public static final int MESSAGE_STOP_RECORD = 102;
    /** Message code for initiating a conversion and upload of the database */
    public static final int MESSAGE_UPLOAD_DATABASE = 103;
    /** Message code for a sample action (used only internally) */
    private static final int MESSAGE_SAMPLE = 104;

    /** The context using this handler */
    private Context context;

    /** The listeners to the recording state */
    private ArrayList<IRecordingListener> recordingListeners = new ArrayList<IRecordingListener>();
    /** Indicate if the handler is currently recording */
    private boolean isRecording = false;

    /** The number of ongoing sample task */
    private int sampleTaskCount = 0;
    /** The maximum number of ongoing sample task */
    private static final int MAX_SAMPLE_TASK = 1;

    /** The number of ongoing upload database task */
    private int uploadTaskCount = 0;
    /** The maximum number of ongoing sample task */
    private static final int MAX_UPLOAD_TASK = 1;

    /** The delay between samples */
    private int sampleRate;

    /**
     * Indicate if the handler is waiting for task to finish before closing the
     * database
     */
    private boolean pendingClose = false;

    /** The SQLConnector used for all the database operations */
    private SQLConnector connector;

    public RecordingHandler(Context context, int sampleRate) {
        this.sampleRate = sampleRate;
        this.context = context;
        this.connector = new SQLConnector(context);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        // Identify the message code
        switch (msg.what) {
        case MESSAGE_START_RECORD:
            startRecord();
            break;
        case MESSAGE_STOP_RECORD:
            stopRecord();
            break;
        case MESSAGE_UPLOAD_DATABASE:
            break;
        case MESSAGE_SAMPLE:
            break;
        }
    }

    /**
     * Action performed when a MESSAGE_START_RECORD is sent. Might fail due to
     * connector failing to open.
     */
    private void startRecord() {
        // If not already recording
        if (!isRecording) {
            try {
                // If we need to open the database
                if (!connector.isOpen())
                    connector.open();
                // Start the sampling now
                this.sendEmptyMessage(MESSAGE_SAMPLE);
                // We are now recording
                isRecording = true;
            } catch (SQLException exc) {
                Log.e("RecordingService", "Can't open SQLConnector", exc);
                // Toast the user that recording won't be available
                Toast.makeText(context, R.string.error_open_sql_connector,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Action performed when a MESSAGE_STOP_RECORD is sent.
     */
    private void stopRecord() {
        // If actually recording
        if (isRecording) {
            try {
                // If we need to open the database
                if (!connector.isOpen())
                    connector.open();
                // Start the sampling now
                this.sendEmptyMessage(MESSAGE_SAMPLE);
                // We are now recording
                isRecording = true;
            } catch (SQLException exc) {
                // Connector opening failed
                Log.e(RecordingHandler.class.toString(), "handleMessage", exc);
            }
        }
    }

    /** Indicate if a recording process is in progress */
    public boolean isRecording() {
        return this.isRecording;
    }

    /** Add a recording listener */
    public void register(IRecordingListener listener) {
        // Add it to the list
        recordingListeners.add(listener);
        // Notify it immediatly
        listener.onRecordingChanged(isRecording);
    }

    /** Remove a recording listener */
    public void unregister(IRecordingListener listener) {
        // Remove it from the list
        recordingListeners.remove(listener);
    }

    /** Notify the recording process state has changed */
    private void notifyRecording(boolean state) {
        // Notify every listener
        for (IRecordingListener listener : recordingListeners) {
            // For each listener, notify
            listener.onRecordingChanged(state);
        }
    }

    private class SampleTask extends AsyncTask<Sample, Void, Void> {
        @Override
        protected Void doInBackground(Sample... params) {
            // Get the passed parameters
            Sample parameters = params[0];
            // Insert the measure in the database
            try {
                connector.insertMeasure(parameters.measureContext,
                        parameters.data, parameters.latitude,
                        parameters.longitude);
                Log.d("SampleTask",
                        "Insertion effectuée :\n" + parameters.data.toString());
            } catch (DataBaseException e) {
                Log.e("SampleTask", "DataBaseException", e);
            } catch (CollectMeasureException e) {
                Log.e("SampleTask", "CollectMeasureException", e);
            }
            return null;
        }

    }

    /**
     * Convert the whole database to a custom CSV file and try to upload this
     * file with the prefered protocols
     */
    public void uploadDatabase() {
        FileReadyListener writingCallback = new WritingCallbackPreferences(0); // TODO
        String comments = "...comments about file...";
        FileGenerator writer = new FileGenerator(connector, comments,
                writingCallback);
        writer.execute();
    }

    private class WritingCallbackPreferences implements FileReadyListener {

        private int chosenProtocol;

        public WritingCallbackPreferences(int protocol) {
            this.chosenProtocol = protocol;
        }

        @Override
        public void onFileReady(ByteArrayOutputStream file) {
            // TODO : make recording run again if it was running before calling
            // uploadDatabase()
            if (file == null) {
                // No data waiting to be uploaded : toast it
                Toast.makeText(context, R.string.error_no_data_to_upload,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Creation of a sending CallBack : called when one sending is
                // done : if file had not been send it is stored into app file
                // systeme
                SendCompleteListener sendingCallback = new SendCompleteListener() {
                    @Override
                    public void onTaskCompleted(String protocole,

                    File fileSent, boolean success) {
                        if (!success) {// if files can't be send, it's stored
                                       // into internal storage:
                            Toast.makeText(context,
                                    R.string.error_sending_file,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context,
                                    R.string.information_upload_succeeded,
                                    Toast.LENGTH_SHORT).show();
                            //archive is deleted
                            fileSent.delete();
                        }

                    }
                };
                // generating file name with timestamp to preserve unicity
                String name = "file" + System.currentTimeMillis();
                // adding file to archive
                try {
                    addFileToArchive(name, file);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // sending archive
                File archive = new File(context.getFilesDir(),
                        GlobalConstants.ARCHIVE_NAME);
                if (this.chosenProtocol == GlobalConstants.SENDING_PROTOCOL_HTTP) {
                    // setting server URL 
                    String url = GlobalConstants.URL_SERVER_HTTP;
                    // creation and execution of a DataSendingManager 
                    DataSendingManager managerHTTP = new DataSendingManager(
                            url, archive, "http", sendingCallback);
                    managerHTTP.execute();
                } else if (this.chosenProtocol == GlobalConstants.SENDING_PROTOCOL_FTP) {
                    // setting server URL 
                    String url = GlobalConstants.URL_SERVER_FTP;
                    // creation and execution of a DataSendingManager
                    DataSendingManager managerFTP = new DataSendingManager(url,
                            archive, "ftp", sendingCallback);
                    managerFTP.execute();
                }
            }
        }
    }

    /**
     * Add file into pending archive
     * 
     * @throws IOException
     */
    public void addFileToArchive(String fileName,
            ByteArrayOutputStream fileContent) throws IOException {

        File archive = new File(context.getFilesDir(),
                GlobalConstants.ARCHIVE_NAME);
        FileOutputStream archiveStream;
        archiveStream = new FileOutputStream(archive);
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
                archiveStream));
        String filename = fileName;
        byte[] bytes = (fileContent.toByteArray());
        ZipEntry entry = new ZipEntry(filename);
        zos.putNextEntry(entry);
        zos.write(bytes);
        zos.closeEntry();
        zos.close();

    }

}
