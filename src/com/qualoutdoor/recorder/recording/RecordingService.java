package com.qualoutdoor.recorder.recording;

import java.util.ArrayList;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.qualoutdoor.recorder.LocalBinder;
import com.qualoutdoor.recorder.notifications.NotificationCenter;

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
    /** A fake recording thread */
    private Thread thread;

    @Override
    public void onCreate() {
        // Initialize a RecordingBinder that knows this Service
        mRecordingBinder = new LocalBinder<RecordingService>(this);
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

    /** Set the sampling rate to the specified value in milliseconds */
    public void setSamplingRate(int millis) {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRecording) {
            // Start the recording thread
            startRecording();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (isRecording) {
            // Stop the recording process
            stopRecording();
        }
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
        // TODO actually start the thread
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Log.d("Recording Thread", "Tic");
                        sleep(3000);
                        Log.d("Recording Thread", "Toc");
                        sleep(3000);
                    }
                } catch (InterruptedException e) {
                    Log.d("Recording Thread", "Interrupted exception");
                }
            }
        };
        thread.start();

        // Notify the listeners
        notifyRecording();
    }

    /** Stop the recording process */
    public void stopRecording() {
        // TODO stop the thread
        thread.interrupt();
        thread = null;
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
}
