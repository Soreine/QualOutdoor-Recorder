package com.qualoutdoor.recorder.recording;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.qualoutdoor.recorder.LocalServiceConnection;

/**
 * An implementation for a local ServiceConnection to the RecordingService.
 * Caution : this necessitate the binder to be cast into RecordingBinder on
 * connection. This works because our services belong to the same process.
 */
public abstract class RecordingServiceConnection extends LocalServiceConnection implements ServiceConnection {


	/** The recording service we connected to */
	private RecordingService service;

	public RecordingServiceConnection() {
		super();
	}
	
	@Override
	public void onServiceConnected(ComponentName serviceName, IBinder binder) {
		// Retrieve the service
		service = ((RecordingBinder) binder).getService(); 
		Log.d("RecordingServiceConnection", "onServiceConnected");
		super.onServiceConnected(serviceName, binder);
	}

	/** Get the binder */
	public RecordingService getService() {
		return service;
	}

}