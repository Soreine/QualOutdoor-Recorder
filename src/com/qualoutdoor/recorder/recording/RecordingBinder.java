package com.qualoutdoor.recorder.recording;

import android.os.Binder;

/**
 * The local binder for the RecordingService. Caution, this works only if the
 * service and the component binding to it are in the same process.
 */
public class RecordingBinder extends Binder {

	/** The service that provided this binder */
	private RecordingService myService;

	/** A constructor for the binder to know its service */
	public RecordingBinder(RecordingService service) {
		super();
		myService = service;
	}

	/** Get the service that provided this binder */
	public RecordingService getService() {
		return myService;
	}
}
