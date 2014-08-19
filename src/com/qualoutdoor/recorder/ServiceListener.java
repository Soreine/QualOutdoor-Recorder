package com.qualoutdoor.recorder;

import android.app.Service;

/**
 * A listener class to monitor when a service S becomes available
 * 
 * @author Gaborit Nicolas
 */
public interface ServiceListener<S extends Service> {
    /** Callback made when the service becomes available */
    void onServiceAvailable(S service);
}
