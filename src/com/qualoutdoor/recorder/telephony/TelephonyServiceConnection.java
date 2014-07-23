package com.qualoutdoor.recorder.telephony;

import android.content.ComponentName;
import android.os.IBinder;
import android.util.Log;

import com.qualoutdoor.recorder.LocalServiceConnection;

/**
 * An implementation for a local ServiceConnection to the TelephonyService.
 * Caution : this necessitate the binder to be cast into TelephonyBinder on
 * connection. This works because our services belong to the same process.
 */
public abstract class TelephonyServiceConnection extends LocalServiceConnection  {


	/** The telephony service we connected to */
	private TelephonyService service;

	public TelephonyServiceConnection() {
		super();
	}
	
	@Override
	public void onServiceConnected(ComponentName serviceName, IBinder binder) {
		// Retrieve the service
		service = ((TelephonyBinder) binder).getService(); 
		Log.d("TelephonyServiceConnection", "onServiceConnected");
		super.onServiceConnected(serviceName, binder);
	}

	/** Get the binder */
	public TelephonyService getService() {
		return service;
	}

}
