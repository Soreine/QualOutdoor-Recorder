package com.qualoutdoor.recorder;

/** A listener class to monitor when a service S becomes available */
public interface ServiceListener<S> {
    /** Callback made when the service becomes available */
    void onServiceAvailable(S service);
}
