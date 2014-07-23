package com.qualoutdoor.recorder.recording;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * An implementation for a local ServiceConnection to the RecordingService.
 * Caution : this necessitate the binder to be cast into RecordingBinder on
 * connection. This works because our services belong to the same process.
 */
public class RecordingServiceConnection implements ServiceConnection {

	/** Indicate if the service is bound or not */
	private boolean isBound;

	/** The binder received from the service */
	private RecordingBinder binder;

	public RecordingServiceConnection() {
		super();
		isBound = false;
		binder = null;
	}

	@Override
	public void onServiceConnected(ComponentName serviceName, IBinder binder) {
		// Hold a reference to the binder
		this.binder = (RecordingBinder) binder;
		// We have bound to the service
		isBound = true;
	}

	@Override
	public void onServiceDisconnected(ComponentName serviceName) {
		// We are not bound anymore to the service
		isBound = false;
	}

	/** Are we bound to the service ? */
	public boolean isBound() {
		return isBound;
	}
	
	/** Get the binder */
	public RecordingBinder getBinder() {
		return binder;
	}
}
