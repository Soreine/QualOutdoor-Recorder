package com.qualoutdoor.recorder.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.qualoutdoor.recorder.LocalBinder;
import com.qualoutdoor.recorder.location.ILocation;

/**
 * This service is an Android implementation of ITelephony, it uses a
 * TelephonyManager to access phone state informations. An app component can
 * bind to it anytime in order to monitor the phone state.
 */
public class LocationService extends Service implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    /** The default value asked for location update (millis) */
    private static final int DEFAULT_LOCATION_UPDATE_INTERVAL = 2000;
    /** The fastest location update interval we can handle (millis) */
    private static final int FASTEST_LOCATION_INTERVAL = 1000;

    /** The interface binder for this service */
    private IBinder mBinder;

    /** The current location */
    private ILocation location;

    // Note : Might use CopyOnWriteArrayList to avoid
    // ConcurrentModificationExceptions if a
    // listener attempts to remove itself during event notification.
    /** Store the listeners listening to LISTEN_LOCATION */
    private ArrayList<LocationListener> listenersLocation = new ArrayList<LocationListener>();

    /** Our location request reference */
    private final LocationRequest locationRequest;
    {
        // Create a location request object
        locationRequest = LocationRequest.create();
        // Set accuracy to high
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval equal to default
        locationRequest.setInterval(DEFAULT_LOCATION_UPDATE_INTERVAL);
        // Set the fastest update interval
        locationRequest.setFastestInterval(FASTEST_LOCATION_INTERVAL);
    }

    /** Flag that indicates if a location request is underway */
    private boolean locationInProgress;

    /** Our location client reference */
    private LocationClient locationClient;

    @Override
    public void onCreate() {
        // No location request is underway
        locationInProgress = false;
        // Create a new location client using this class to handle callbacks
        locationClient = new LocationClient(this, this, this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return our interface binder
        return mBinder;
    }

    public ILocation getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    /** Register the given LocationListener to receive location updates */
    public void listen(LocationListener listener) {
                // The listener wish to monitor the location, add it to the list
                listenersLocation.add(listener);
                // TODO Notify it immediatly with the current data
    }

    // TODO
    public void setMinimumRefreshRate(int milliseconds) {
        // TODO Auto-generated method stub
    }

    /** Notify each location listeners with the current ILocation value */
    private void notifyLocationListeners(ILocation location) {
        for (ILocationListener listener : listenersCellInfo) {
            // For each listener, notify
            listener.onLocationChanged(location);
        }
    }

    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub
    }

    /** Check whether the Google Play Services are available */
    private boolean areServicesConnected() {
        // Check that Google Play services are available
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // Return if Google Play services are available
        return (ConnectionResult.SUCCESS == resultCode);
    }
}
