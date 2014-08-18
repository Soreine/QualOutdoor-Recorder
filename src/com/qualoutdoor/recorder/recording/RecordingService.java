package com.qualoutdoor.recorder.recording;

import java.util.LinkedList;
import java.util.List;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

import com.qualoutdoor.recorder.GlobalConstants;
import com.qualoutdoor.recorder.LocalBinder;
import com.qualoutdoor.recorder.LocalServiceConnection;
import com.qualoutdoor.recorder.R;
import com.qualoutdoor.recorder.location.LocationService;
import com.qualoutdoor.recorder.notifications.NotificationCenter;
import com.qualoutdoor.recorder.persistent.MeasureContext;
import com.qualoutdoor.recorder.persistent.Sample;
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

    /** The TelephonyServiceConnection used to access the TelephonyService */
    private LocalServiceConnection<TelephonyService> telServiceConnection = new LocalServiceConnection<TelephonyService>(
            TelephonyService.class);
    /** The LocationServiceConnection used to access the LocationService */
    private LocalServiceConnection<LocationService> locServiceConnection = new LocalServiceConnection<LocationService>(
            LocationService.class);

    /** The database context */
    private MeasureContext databaseContext;
    /** The sampling rate in milliseconds */
    private int sampleRate;
    /** The list of the measures to record */
    private List<Integer> metrics;

    /** The recording handler */
    private RecordingHandler handler;

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

        // Initialize the measure context
        databaseContext = new MeasureContext();

        // Initialize the RecordingHandler
        handler = new RecordingHandler(this, sampleRate);

        // Bind to the telephony and location services
        telServiceConnection.bindToService(this);
        locServiceConnection.bindToService(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return our interface binder
        return mRecordingBinder;
    }

    // TODO
    /** Set the sampling rate to the specified value in milliseconds */
    public void setSamplingRate(int millis) {}

    /** Start the recording process. */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create the notification that will be displayed
        Notification notification = NotificationCenter
                .getRecordingNotification(this);
        // Notify we are running in foreground
        startForeground(NotificationCenter.BACKGROUND_RECORDING, notification);
        // Start the sampling process
        handler.sendEmptyMessage(RecordingHandler.MESSAGE_START_RECORD);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // Unbind from the TelephonyService if needed
        unbindService(telServiceConnection);
        // Unbind from the LocationService if needed
        unbindService(locServiceConnection);
        super.onDestroy();
    }

    /** Stop the recording process */
    public void stopRecording() {
        // Send a message to the handler in order to stop recording
        handler.sendEmptyMessage(RecordingHandler.MESSAGE_STOP_RECORD);
        // Stop being foreground as we are no longer recording and remove
        // notification
        stopForeground(true);
        // Stop self
        stopSelf();
    }

    /** Indicates whether the service is currently recording data */
    public boolean isRecording() {
        // Hand over the call
        return handler.isRecording();
    }

    /** Add a recording listener */
    public void register(IRecordingListener listener) {
        // Hand over the call to the handler
        handler.register(listener);
    }

    /** Remove a recording listener */
    public void unregister(IRecordingListener listener) {
        // Hand over the call to the handler
        handler.unregister(listener);
    }

    /**
     * Fetch the current telephony data, and return the Sample object used for
     * insertion by a SampleTask
     */
    public Sample sample() throws SampleFailedException {
        // Check that the services are available
        if (!locServiceConnection.isAvailable()
                || !telServiceConnection.isAvailable()) {
            // Not able to sample
            throw new SampleFailedException(
                    "Telephony services and/or Location services unavailable");
        }
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
            throw new SampleFailedException("Location was outdated");
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
            throw new SampleFailedException("Could not find primary cell");
        }

        // Update the database context
        databaseContext.set(MeasureContext.GROUP_INDEX, GlobalConstants.group);
        databaseContext.set(MeasureContext.USER_INDEX, GlobalConstants.user);
        databaseContext.set(MeasureContext.MCC_INDEX, primaryCell.getMcc());
        databaseContext.set(MeasureContext.MNC_INDEX, primaryCell.getMnc());
        databaseContext.set(MeasureContext.NTC_INDEX,
                telService.getNetworkType());

        // Fetch the telephony measures
        // Create the data hashmap
        SparseArray<String> dataList = new SparseArray<String>(metrics.size());

        // Fill the fields
        for (Integer field : metrics) {
            String value = "";
            switch (field) {
            case GlobalConstants.FIELD_CALL:
                // Unimplemented
                value = "TODO";
                break;
            case GlobalConstants.FIELD_CELL_ID:
                value += primaryCell.getCid();
                break;
            case GlobalConstants.FIELD_SIGNAL_STRENGTH:
                value += primaryCell.getSignalStrength().getDbm();
                break;
            case GlobalConstants.FIELD_DOWNLOAD:
                value = "TODO";
                break;
            case GlobalConstants.FIELD_UPLOAD:
                value = "TODO";
                break;
            }
            // Insert in the database
            dataList.put(field, value);
        }

        // Return the newly created Sample object
        return new Sample(databaseContext.clone(), dataList,
                location.getLatitude(), location.getLongitude());
    }

    /**
     * Convert the whole database to a custom CSV file and try to upload this
     * file with the prefered protocols
     */
    public void uploadDatabase() {
        // Get the upload preferences from the SharedPreferences (default to
        // false)
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        // Get the chosen upload protocol
        int chosenProtocol = Integer.parseInt(prefs.getString(
                getString(R.string.pref_key_protocol), getResources()
                        .getString(R.string.pref_default_protocol)));
        // Get a message for the handler containing the chosenProtocol
        Message msg = handler.obtainMessage(
                RecordingHandler.MESSAGE_UPLOAD_DATABASE, chosenProtocol, 0);
        handler.sendMessage(msg);
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
