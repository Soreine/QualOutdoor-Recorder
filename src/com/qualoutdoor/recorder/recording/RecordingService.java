package com.qualoutdoor.recorder.recording;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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

	@Override
	public void onCreate() {
		// Initialize a RecordingBinder that knows this Service
		mRecordingBinder = new RecordingBinder(this);
		// Initialize the recording state
		isRecording = false;
		super.onCreate();
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

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Start the recording thread
		startRecording();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Stop the recording thread
		super.onDestroy();
	}

	private void startRecording() {
		// Update the recording state
		isRecording = true;
		// Create the notification that will be displayed
		Notification notification = NotificationCenter
				.getRecordingNotification(this);
		// Notify we are running in foreground
		startForeground(NotificationCenter.BACKGROUND_RECORDING, notification);
		// TODO actually start the thread
	}
}
