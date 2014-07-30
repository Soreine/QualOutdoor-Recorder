package com.qualoutdoor.recorder;

import android.os.Binder;

/**
 * A local binder for a local service S. Caution, this works only if the
 * service and the component binding to it are in the same process.
 */
public class LocalBinder<S> extends Binder {

	/** The service that provided this binder */
	private S myService;

	/** A constructor for the binder to know its service */
	public LocalBinder(S service) {
		super();
		myService = service;
	}

	/** Get the service that provided this binder */
	public S getService() {
		return myService;
	}
}
