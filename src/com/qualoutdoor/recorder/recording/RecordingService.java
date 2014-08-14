package com.qualoutdoor.recorder.recording;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.qualoutdoor.recorder.GlobalConstants;
import com.qualoutdoor.recorder.LocalBinder;
import com.qualoutdoor.recorder.LocalServiceConnection;
import com.qualoutdoor.recorder.R;
import com.qualoutdoor.recorder.location.LocationService;
import com.qualoutdoor.recorder.network.DataSendingManager;
import com.qualoutdoor.recorder.network.EmailFileSender;
import com.qualoutdoor.recorder.network.FileToUpload;
import com.qualoutdoor.recorder.network.SendCompleteListener;
import com.qualoutdoor.recorder.notifications.NotificationCenter;
import com.qualoutdoor.recorder.persistent.CollectMeasureException;
import com.qualoutdoor.recorder.persistent.DataBaseException;
import com.qualoutdoor.recorder.persistent.FileGenerator;
import com.qualoutdoor.recorder.persistent.FileReadyListener;
import com.qualoutdoor.recorder.persistent.MeasureContext;
import com.qualoutdoor.recorder.persistent.SQLConnector;
import com.qualoutdoor.recorder.telephony.ICellInfo;
import com.qualoutdoor.recorder.telephony.TelephonyService;

/**
 * This service when started will link to the TelephonyService and begin
 * sampling the phone state. One can bind to this service in order to modify the
 * sampling rate. It is needed to call stopService() on it in order to stop the
 * recording process.
 */
public class RecordingService extends Service {

    /** The interface binder for this service */
    private IBinder mRecordingBinder;
    /** Indicates if a recording process is ongoing */
    private boolean isRecording = false;

    /** The listeners to the recording state */
    private ArrayList<IRecordingListener> recordingListeners = new ArrayList<IRecordingListener>();

    /** The TelephonyServiceConnection used to access the TelephonyService */
    private LocalServiceConnection<TelephonyService> telServiceConnection = new LocalServiceConnection<TelephonyService>(
            TelephonyService.class);
    /** The LocationServiceConnection used to access the LocationService */
    private LocalServiceConnection<LocationService> locServiceConnection = new LocalServiceConnection<LocationService>(
            LocationService.class);

    /** The SQL connector */
    private SQLConnector connector;
    /** Indicate if the database has been opened successfully */
    private volatile boolean isDBavailable = false;
    /** The database context */
    private MeasureContext databaseContext;
    /** The sampling rate in milliseconds */
    private int sampleRate;
    /** The list of the measures to record */
    private List<Integer> metrics;

    

    /** The recording handler */
    private final RecordingHandler handler = new RecordingHandler();


    /** This runnable defines the action to do on a sampling event */
    public final Runnable samplingRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                // If we are still recording
                if (isRecording) {
                    // Refresh all the telephony data
                    sample(metrics);
                }
            } catch (Exception exc) {
                // Log the error
                Log.e("SamplingRunnable", "", exc);
            } finally {
                // If we are still recording
                if (isRecording) {
                    // Call again later
                    handler.postDelayed(this, sampleRate);
                }
            }
        }
    };

    @Override
    public void onCreate() {
        // Initialize a RecordingBinder that knows this Service
        mRecordingBinder = new LocalBinder<RecordingService>(this);

        // Get the sample rate preference
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        sampleRate = prefs.getInt(
                getString(R.string.pref_key_display_sampling_rate),
                getResources().getInteger(R.integer.default_sampling_rate))
                * GlobalConstants.MILLIS_IN_SECOND;

        // Get the metrics preferences
        metrics = getMetricPreferences(prefs);

        // The database is not available yet
        isDBavailable = false;

        // Initialize the data base
        try {
            // Initialize the SQL connector
            this.connector = new SQLConnector(this);

            // Open the database
            this.connector.open();// la bdd est gener�e � partir du cr�ateur
            // Database is available
            isDBavailable = true;

            // Initialize the database context
            databaseContext = new MeasureContext();

        } catch (DataBaseException exc) {
            Log.e("RecordingService", "Can't initialize SQLConnector", exc);
            // Toast the user that recording won't be available
            Toast.makeText(this, R.string.error_initialize_sql_connector,
                    Toast.LENGTH_SHORT).show();
        } catch (SQLException exc) {
            Log.e("RecordingService", "Can't open SQLConnector", exc);
            // Toast the user that recording won't be available
            Toast.makeText(this, R.string.error_open_sql_connector,
                    Toast.LENGTH_SHORT).show();
        }

        // Bind to the telephony and location services
        telServiceConnection.bindToService(this);
        locServiceConnection.bindToService(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return our interface binder
        return mRecordingBinder;
    }

    /** Indicates whether the service is currently recording data */
    public boolean isRecording() {
        return isRecording;
    }

    // TODO
    /** Set the sampling rate to the specified value in milliseconds */
    public void setSamplingRate(int millis) {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The database is available
        if (!isRecording) {

            if (!isDBavailable) {
                // Unable to start recording : toast it
                Toast.makeText(this, R.string.error_recording_unavailable,
                        Toast.LENGTH_SHORT).show();
                // Send a message to stop this service immediatly
                stopSelf();
            } else {
                // Start the recording thread
                startRecording();
                // Ask for a short enough update rate of location
                locServiceConnection.getService().setMinimumRefreshRate(
                        sampleRate);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (isRecording) {
            // Stop the recording process
            stopRecording();
        }
        // Close the database connector
        connector.close();
        // The database is no more available
        isDBavailable = false;
        // Unbind from the TelephonyService if needed
        unbindService(telServiceConnection);
        // Unbind from the LocationService if needed
        unbindService(locServiceConnection);
        super.onDestroy();
    }

    /** Start the recording process. */
    private void startRecording() {
        // Update the recording state
        isRecording = true;
        // Create the notification that will be displayed
        Notification notification = NotificationCenter
                .getRecordingNotification(this);
        // Notify we are running in foreground
        startForeground(NotificationCenter.BACKGROUND_RECORDING, notification);

        // Start the sampling process
        handler.post(samplingRunnable);

        // Notify the listeners
        notifyRecording();
    }

    /** Stop the recording process */
    public void stopRecording() {
        // Update the recording state
        isRecording = false;
        // Notify the listeners
        notifyRecording();
        // Stop being foreground as we are no longer recording and remove
        // notification
        stopForeground(true);
        // Stop self
        stopSelf();
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

    /** Notify all the recording listener */
    private void notifyRecording() {
        for (IRecordingListener listener : recordingListeners) {
            // For each listener, notify
            listener.onRecordingChanged(isRecording);
        }
    }

    /**
     * Fetch the current telephony data, and make an insertion in the database
     */
    private void sample(List<Integer> fields) {
        // Check that the services are available
        if (locServiceConnection.isAvailable()
                && telServiceConnection.isAvailable()) {
            // Get the services
            TelephonyService telService = telServiceConnection.getService();
            LocationService locService = locServiceConnection.getService();

            // Fetch the location
            Location location = locService.getLocation();

            long now = System.currentTimeMillis();
            long age = now - location.getTime();

            if (age > 2 * sampleRate) {
                // The data are too old
                Log.d("SamplingRunnable", "Too old : " + age);
                return;
            }

            // The primary cell
            ICellInfo primaryCell = null;
            // Get all the cell infos
            List<ICellInfo> cellInfos = telService.getAllCellInfo();
            // Find the registered cell
            for (ICellInfo cell : cellInfos) {
                // If primary cell
                if (cell.isRegistered()) {
                    primaryCell = cell;
                    break;
                }
            }

            if (primaryCell == null) {
                // We are not able to fetch the desired data
                return;
            }

            // Update the database context
            databaseContext.set(MeasureContext.GROUP_INDEX,
                    GlobalConstants.group);
            databaseContext
                    .set(MeasureContext.USER_INDEX, GlobalConstants.user);
            databaseContext.set(MeasureContext.MCC_INDEX, primaryCell.getMcc());
            databaseContext.set(MeasureContext.MNC_INDEX, primaryCell.getMnc());
            databaseContext.set(MeasureContext.NTC_INDEX,
                    telService.getNetworkType());

            // Fetch the telephony measures
            // Create the data hashmap
            HashMap<Integer, String> dataList = new HashMap<Integer, String>(
                    fields.size());

            // Fill the fields
            for (Integer field : fields) {
                String value = "";
                switch (field) {
                case GlobalConstants.FIELD_CALL:
                    // Unimplemented
                    value = "unimplemented";
                    break;
                case GlobalConstants.FIELD_CELL_ID:
                    value += primaryCell.getCid();
                    break;
                case GlobalConstants.FIELD_SIGNAL_STRENGTH:
                    value += primaryCell.getSignalStrength().getDbm();
                    break;
                case GlobalConstants.FIELD_DOWNLOAD:
                    value = "unimplemented";
                    break;
                case GlobalConstants.FIELD_UPLOAD:
                    value = "unimplemented";
                    break;
                }
                // Insert in the database
                dataList.put(field, value);
            }

            // Create an AsyncTask for insertion and execute (we clone the
            // MeasureContext for thread separation)
            new SampleTask().execute(new SampleParam(databaseContext.clone(),
                    dataList, location.getLatitude(), location.getLongitude()));

        }
    }

    /** A class that encapsulate the parameters for the SampleTask */
    private class SampleParam {
        public MeasureContext measureContext;
        public HashMap<Integer, String> dataList;
        public double latitude;
        public double longitude;

        public SampleParam(MeasureContext context,
                HashMap<Integer, String> data, double lat, double longi) {
            this.measureContext = context;
            this.dataList = data;
            this.latitude = lat;
            this.longitude = longi;
        }
    }

    private class SampleTask extends AsyncTask<SampleParam, Void, Void> {
        @Override
        protected Void doInBackground(SampleParam... params) {
            // Get the passed parameters
            SampleParam parameters = params[0];
            // Insert the measure in the database
            try {
                connector.insertMeasure(parameters.measureContext,
                        parameters.dataList, parameters.latitude,
                        parameters.longitude);
                Log.d("SampleTask", "Insertion effectuée :\n"
                        + parameters.dataList.toString());
            } catch (DataBaseException e) {
                Log.e("SampleTask", "DataBaseException", e);
            } catch (CollectMeasureException e) {
                Log.e("SampleTask", "CollectMeasureException", e);
            }
            return null;
        }

    }

    /**
     * Convert the whole database to a custom CSV file and upload this file with
     * the prefered protocols
     */
    public void uploadDatabase() {
        if (!isRecording) {
            // TODO : stop recording

            // Get the upload preferences from the SharedPreferences (default to
            // false)
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(this);
            boolean httpDesired = prefs.getBoolean(
                    getString(R.string.pref_key_http_upload), false);
            boolean ftpDesired = prefs.getBoolean(
                    getString(R.string.pref_key_ftp_upload), false);
            boolean mailDesired = prefs.getBoolean(
                    getString(R.string.pref_key_mail_upload), false);

            FileReadyListener writingCallback = new WritingCallbackPreferences(
                   0 );//TODO
            String comments = "...comments about file...";
            FileGenerator writer = new FileGenerator(connector, comments,
                    writingCallback);
            writer.execute();
        }

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
                Toast.makeText(RecordingService.this,
                        R.string.error_no_data_to_upload, Toast.LENGTH_SHORT)
                        .show();
            } else {
                // Creation of a sending CallBack : called when one sending is
                // done : if file had not been send it is stored into app file systeme
                SendCompleteListener sendingCallback = new SendCompleteListener() {
                    @Override
                    public void onTaskCompleted(String protocole,

                          File fileSent, boolean success) {
                        if(!success){//if files can't be send, it's stored into internal storage:
                            Toast.makeText(RecordingService.this, R.string.error_sending_file,
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(RecordingService.this, R.string.information_upload_succeeded,
                            Toast.LENGTH_SHORT).show();
                            //TODO : remove archive
                        }

                    }
                };
                // generating file name with timestamp to preserve unicity
                String name = "file" + System.currentTimeMillis();
                //adding file to archive
                try {
                    addFileToArchive(name,file);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                 
               
                //sending archive
                File archive = new File(RecordingService.this.getFilesDir(),GlobalConstants.ARCHIVE_NAME);
                if (this.chosenProtocol==GlobalConstants.SENDING_PROTOCOL_HTTP) {
                    // setting server URL : normaly feching if from constant
                    // Class
                    String url = GlobalConstants.URL_SERVER_HTTP;
                    // creation and execution of a DataSendingManager : printing
                    // widget has to be resolved
                    DataSendingManager managerHTTP = new DataSendingManager(
                            url, archive , "http", sendingCallback);
                    managerHTTP.execute();
                } else if (this.chosenProtocol==GlobalConstants.SENDING_PROTOCOL_FTP) {
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
     * @throws IOException 
     */
    public void addFileToArchive(String fileName,ByteArrayOutputStream fileContent) throws IOException{
        
        File archive = new File(RecordingService.this.getFilesDir(),GlobalConstants.ARCHIVE_NAME);
        FileOutputStream archiveStream;
        archiveStream = new FileOutputStream(archive);
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(archiveStream));
        String filename = fileName;
        byte[] bytes = (fileContent.toByteArray());
        ZipEntry entry = new ZipEntry(filename);
        zos.putNextEntry(entry);
        zos.write(bytes);
        zos.closeEntry();
        zos.close();
        
    }
    
    
    
    /**
     * Parse the given shared preferences and return the list of the metrics to
     * sample as an integer list
     */
    List<Integer> getMetricPreferences(SharedPreferences prefs) {
        // Create an empty list
        LinkedList<Integer> result = new LinkedList<Integer>();

        // The preference id list
        String[] preferenceKeys = {
                getString(R.string.pref_key_sample_cell_id),
                getString(R.string.pref_key_sample_signal_strength),
                getString(R.string.pref_key_sample_call),
                getString(R.string.pref_key_sample_upload),
                getString(R.string.pref_key_sample_download)
        };
        // The corresponding code
        int[] codes = {
                GlobalConstants.FIELD_CELL_ID,
                GlobalConstants.FIELD_SIGNAL_STRENGTH,
                GlobalConstants.FIELD_CALL, GlobalConstants.FIELD_UPLOAD,
                GlobalConstants.FIELD_DOWNLOAD
        };
        // For each preference, add the corresponding integer code if true
        for (int i = 0; i < preferenceKeys.length; i++) {
            if (prefs.getBoolean(preferenceKeys[i], false))
                result.add(codes[i]);
        }
        return result;
    }
}
