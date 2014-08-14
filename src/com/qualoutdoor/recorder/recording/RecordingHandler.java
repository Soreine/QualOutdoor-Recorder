package com.qualoutdoor.recorder.recording;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

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
import com.qualoutdoor.recorder.network.EmailFileSender;
import com.qualoutdoor.recorder.network.FileToUpload;
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
        FileReadyListener writingCallback = new WritingCallbackPreferences(
                false, false, false); // TODO
        String comments = "...comments about file...";
        FileGenerator writer = new FileGenerator(connector, comments,
                writingCallback);
        writer.execute();
    }

    private class WritingCallbackPreferences implements FileReadyListener {

        private boolean httpDesired;
        private boolean ftpDesired;
        private boolean mailDesired;

        public WritingCallbackPreferences(boolean isHttpDesired,
                boolean isFtpDesired, boolean isMailDesired) {
            this.httpDesired = isHttpDesired;
            this.ftpDesired = isFtpDesired;
            this.mailDesired = isMailDesired;
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
                // done
                SendCompleteListener sendingCallback = new SendCompleteListener() {
                    @Override
                    public void onTaskCompleted(String protocole,

                    HashMap<String, FileToUpload> filesSended, boolean success) {
                        if (!success) {
                            // TODO : store file ....
                        }

                    }
                };

                // Prepartion of the hashmap that contain the file to be sent
                HashMap<String, FileToUpload> filesToSend = new HashMap<String, FileToUpload>();
                // generating file name with timestamp to preserve unicity
                String name = "file" + System.currentTimeMillis();
                // getting input stream from the ByteArrayOutputStream recieved
                InputStream content = new ByteArrayInputStream(
                        file.toByteArray());
                // creating file object
                FileToUpload monFichier = new FileToUpload(name, content);
                // inserting file into hasmap referenced with a name;
                filesToSend.put("uploadedFile", monFichier);

                if (httpDesired) {
                    // setting server URL : normaly feching if from constant
                    // Class
                    String url = GlobalConstants.URL_SERVER_HTTP;
                    // creation and execution of a DataSendingManager : printing
                    // widget has to be resolved
                    DataSendingManager managerHTTP = new DataSendingManager(
                            url, filesToSend, "http", sendingCallback);
                    managerHTTP.execute();
                } else if (ftpDesired) {
                    // setting server URL : normaly feching if from constant
                    // Class
                    String url = GlobalConstants.URL_SERVER_FTP;
                    // creation and execution of a DataSendingManager : printing
                    // widget has to be resolved
                    DataSendingManager managerFTP = new DataSendingManager(url,
                            filesToSend, "ftp", sendingCallback);
                    managerFTP.execute();
                } else if (mailDesired) {
                    // destination address has to be fetched from setting
                    String url = "";
                    EmailFileSender.sendFileByEmail(context, url, filesToSend);
                }
            }
        }
    }

}
