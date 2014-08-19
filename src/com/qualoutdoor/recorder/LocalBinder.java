package com.qualoutdoor.recorder;

import android.os.Binder;

/**
 * A local binder for a local service S. Caution, this works only if the service
 * and the component binding to it are in the same process.
 * 
 * @author Gaborit Nicolas
 * 
 * @param <S>
 *            The class of the Service for which this Binder is used
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
