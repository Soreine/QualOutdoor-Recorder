package com.qualoutdoor.recorder;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * This ServiceConnection is meant to be used for connecting a component to
 * services within the same process. Indeed we extend
 */
public abstract class LocalServiceConnection implements ServiceConnection {
	/** Indicate if the service is bound or not */
	protected boolean isBound = false;

	/** Are we bound to the service ? */
	public boolean isBound() {
		return isBound;
	}

	@Override
	public void onServiceDisconnected(ComponentName serviceName) {
		// We are not bound anymore to the service
		isBound = false;
		Log.d("LocalServiceConnection", "onServiceDisconnected");
	}

	@Override
	public void onServiceConnected(ComponentName serviceName, IBinder binder) {
		Log.d("LocalServiceConnection", "onServiceConnected");
		// We have bound to the service
		isBound = true;
		// Call onServiceObtained
		onServiceObtained();


	}

	/**
	 * Called to allow the component to receive an instance of the Service
	 * directly. To be overridden in the component's implementation of
	 * LocalServiceConnected.
	 */
	public abstract void onServiceObtained();
}
