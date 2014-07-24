package com.qualoutdoor.recorder.recording;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

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

	/** A fake recording thread */
	private Thread thread;

	@Override
	public void onCreate() {
		Log.d("RecordingService", "onCreate");
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

	/** Set the sampling rate to the specified value in milliseconds */
	public void setSamplingRate(int millis) {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("RecordingService", "onStartCommand");
		if (!isRecording) {
			// Start the recording thread
			startRecording();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d("RecordingService", "onDestroy");
		if (isRecording) {
			// Stop the recording process
			stopRecording();
		}
		super.onDestroy();
	}

	/** Start the recording process. */
	private void startRecording() {
		Log.d("RecordingService", "startRecording");
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
						Log.d("Thread", "Tic");
						sleep(3000);
						Log.d("Thread", "Toc");
						sleep(3000);
					}
				} catch (InterruptedException e) {
					Log.d("Thread", "Interrupted exception");
				}
			}
		};
		thread.start();
	}

	/** Stop the recording process */
	public void stopRecording() {
		Log.d("RecordingService", "stopRecording");
		// TODO stop the thread
		thread.interrupt();
		thread = null;
		// Update the recording state
		isRecording = false;
		// Stop being foreground as we are no longer recording and remove
		// notification
		stopForeground(true);

	}
}
