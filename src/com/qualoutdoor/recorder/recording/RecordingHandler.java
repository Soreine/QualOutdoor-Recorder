package com.qualoutdoor.recorder.recording;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
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

    /** The recording service using this handler */
    private RecordingService recordingService;

    /** The listeners to the recording state */
    private ArrayList<IRecordingListener> recordingListeners = new ArrayList<IRecordingListener>();
    /** Indicate if the handler is currently recording */
    private boolean isRecording = false;

    /** The number of ongoing sample task */
    private int pendingSampleTaskCount = 0;

    /** The number of ongoing upload database task */
    private int uploadTaskCount = 0;

    /** The delay between samples */
    private int sampleRate;

    /**
     * Indicate if the tasks should close the database when done
     */
    private boolean shouldClose = true;

    /** The SQLConnector used for all the database operations */
    private SQLConnector connector;
    /**
     * The semaphore limiting access to the database to one at a time, with
     * fairness (FIFO ordering)
     */
    private final Semaphore databaseSemaphore = new Semaphore(1, true);

    /**
     * Create a new RecordingHandler with the given sampleRate
     * 
     * @param recordingService
     *            The service using this handler
     * @param sampleRate
     *            The initial value of the sample rate
     */
    public RecordingHandler(RecordingService recordingService, int sampleRate) {
        this.sampleRate = sampleRate;
        this.recordingService = recordingService;
        this.connector = new SQLConnector(recordingService);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        // Identify the message code
        switch (msg.what) {
        case MESSAGE_START_RECORD:
            actionStartRecord();
            break;
        case MESSAGE_STOP_RECORD:
            actionStopRecord();
            break;
        case MESSAGE_UPLOAD_DATABASE:
            // Read the chosen protocol
            int chosenProtocol = msg.arg1;
            actionUploadDatabase(chosenProtocol);
            break;
        case MESSAGE_SAMPLE:
            actionSample();
            break;
        }
    }

    /**
     * Action performed when a MESSAGE_START_RECORD is received. Might fail due
     * to connector failing to open.
     */
    private void actionStartRecord() {
        // If not already recording
        if (!isRecording) {
            try {
                // If we need to open the database
                if (!connector.isOpen())
                    connector.open();
                // Start the sampling now
                this.sendEmptyMessage(MESSAGE_SAMPLE);
                pendingSampleTaskCount++;
                // We are now recording
                setNotifyRecording(true);
                // Thus we don't want the database to be closed
                shouldClose = false;
            } catch (SQLException exc) {
                Log.e("RecordingService", "Can't open SQLConnector", exc);
                // Toast the user that recording won't be available
                Toast.makeText(recordingService,
                        R.string.error_open_sql_connector, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * Action performed when a MESSAGE_STOP_RECORD is received.
     */
    private void actionStopRecord() {
        // If actually recording
        if (isRecording) {
            // We will stop the recording process
            setNotifyRecording(false);
            // Thus we should close the database when done
            shouldClose = false;
        }
    }

    /**
     * Action performed when a MESSAGE_UPLOAD_DATABASE is received. Convert the
     * whole database to a custom CSV file and try to upload this file with the
     * chosen protocol
     * 
     * @param chosenProtocol
     *            The protocol used for the upload
     */
    private void actionUploadDatabase(int chosenProtocol) {
        // Open database if needed
        if (!connector.isOpen())
            connector.open();
        // Increment the number of upload task
        uploadTaskCount++;
        // Create a callback that will upload the generated file when done
        FileReadyListener writingCallback = new WritingCallbackPreferences(
                chosenProtocol);
        // Define the comment added at the beginning of the file
        String comments = "...comments about file...";
        // Create a writer that will convert the database into a file
        FileGenerator writer = new FileGenerator(connector, databaseSemaphore, comments,
                writingCallback);
        // Start conversion
        writer.execute();
    }

    /** Action performed when a MESSAGE_SAMPLE is received */
    private void actionSample() {
        // Try to make a sample
        try {
            Sample sample = recordingService.sample();
            // Insert the sample in the database
            new InsertSampleTask().execute(sample);
        } catch (SampleFailedException e) {
            pendingSampleTaskCount--;
            // The sample failed, sample again later
            if (isRecording) {
                this.sendEmptyMessageDelayed(MESSAGE_SAMPLE, sampleRate);
                pendingSampleTaskCount++;
            }
        }
    }

    /**
     * Check that no task will use the database in the future and close it if
     * needed
     */
    private void checkCloseDatabase() {
        // Check that no task are remaining and that we should close the
        // database
        if (shouldClose && (pendingSampleTaskCount + uploadTaskCount == 0)) {
            // Close the database
            if (connector.isOpen())
                connector.close();
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

    /** Change and notify that the recording process state has changed */
    private void setNotifyRecording(boolean state) {
        // Update the recording state
        isRecording = state;
        // Notify every listener
        for (IRecordingListener listener : recordingListeners) {
            // For each listener, notify
            listener.onRecordingChanged(state);
        }
    }

    /**
     * This task insert a given Sample in the database
     */
    private class InsertSampleTask extends AsyncTask<Sample, Void, Void> {
        /** The start time of this task */
        private long startTime;

        @Override
        protected void onPreExecute() {
            // Save the date of execution
            startTime = System.currentTimeMillis();
        }

        @Override
        protected Void doInBackground(Sample... params) {
            // Get the passed parameters
            Sample parameters = params[0];
            // Insert the measure in the database
            try {
                // Acquire access to database
                databaseSemaphore.acquire();
                // Insert the sample data
                connector.insertMeasure(parameters.measureContext,
                        parameters.data, parameters.latitude,
                        parameters.longitude);
                // Release access
                databaseSemaphore.release();
                Log.d("SampleTask",
                        "Insertion effectuée :\n" + parameters.data.toString());
            } catch (DataBaseException e) {
                Log.e("SampleTask", "DataBaseException", e);
            } catch (CollectMeasureException e) {
                Log.e("SampleTask", "CollectMeasureException", e);
            } catch (InterruptedException e) {
                Log.e("SampleTaks", "InterruptedException", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Decrement the task count
            pendingSampleTaskCount--;
            // Should we continue the recording ?
            if (isRecording) {
                // Get the elapsed time
                long elapsedTime = System.currentTimeMillis() - startTime;
                // Compensate the time taken for the insertion
                if (elapsedTime > sampleRate) {
                    // Call again as soon as possible
                    RecordingHandler.this.sendEmptyMessage(MESSAGE_SAMPLE);
                } else {
                    // We are on schedule, call again for the next sample
                    RecordingHandler.this.sendEmptyMessageDelayed(
                            MESSAGE_SAMPLE, sampleRate - elapsedTime);
                }
            } else {
                // Close the database if needed
                checkCloseDatabase();
            }
        }
    }

    /**
     * This object defines the action to be performed when the database has been
     * converted and it should be uploaded
     */
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
                Toast.makeText(recordingService,
                        R.string.error_no_data_to_upload, Toast.LENGTH_SHORT)
                        .show();
            } else {
                // Creation of a sending CallBack : called when one sending is
                // done : if file had not been send it is stored into app file
                // systeme
                SendCompleteListener sendingCallback = new SendCompleteListener() {
                    @Override
                    public void onTaskCompleted(String protocol,

                    File fileSent, boolean success) {
                        if (!success) {// if files can't be send, it's stored
                                       // into internal storage:
                            Toast.makeText(recordingService,
                                    R.string.error_sending_file,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(recordingService,
                                    R.string.information_upload_succeeded,
                                    Toast.LENGTH_SHORT).show();
                            // TODO : remove archive
                        }

                        // The upload task is over
                        uploadTaskCount--;
                        // Check if we should close the database
                        checkCloseDatabase();
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
                File archive = new File(recordingService.getFilesDir(),
                        GlobalConstants.ARCHIVE_NAME);
                if (this.chosenProtocol == GlobalConstants.UPLOAD_PROTOCOL_HTTP) {
                    // setting server URL : normaly feching if from constant
                    // Class
                    String url = GlobalConstants.URL_SERVER_HTTP;
                    // creation and execution of a DataSendingManager : printing
                    // widget has to be resolved
                    DataSendingManager managerHTTP = new DataSendingManager(
                            url, archive, "http", sendingCallback);
                    managerHTTP.execute();
                } else if (this.chosenProtocol == GlobalConstants.UPLOAD_PROTOCOL_FTP) {
                    // setting server URL : normaly feching if from constant
                    // Class
                    String url = GlobalConstants.URL_SERVER_FTP;
                    // creation and execution of a DataSendingManager : printing
                    // widget has to be resolved
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

        File archive = new File(recordingService.getFilesDir(),
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
