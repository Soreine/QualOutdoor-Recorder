package com.qualoutdoor.recorder.telephony;

import android.os.Binder;

/**
 * The local binder for the TelephonyService. Caution, this works only if the
 * service and the component binding to it are in the same process.
 */
public class TelephonyBinder extends Binder {

	/** The service that provided this binder */
	private TelephonyService myService;

	/** A constructor for the binder to know its service */
	public TelephonyBinder(TelephonyService service) {
		super();
		myService = service;
	}

	/** Get the service that provided this binder */
	public TelephonyService getService() {
		return myService;
	}
}
