package com.qualoutdoor.recorder.location;

/**
 * A listener interface for receiving Location updates. Override the callback to
 * implements your specific behavior.
 * 
 * @author Gaborit Nicolas
 */
public interface ILocationListener {
    /** Callback invoked when device location changes. */
    void onLocationChanged(ILocation location);

}