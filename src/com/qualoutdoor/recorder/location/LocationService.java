package com.qualoutdoor.recorder.location;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.qualoutdoor.recorder.Debug;
import com.qualoutdoor.recorder.LocalBinder;

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
    private Location location;

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

    /** Indicates if Google Play Services are available */
    private boolean servicesAvailable;

    /** Our location client reference */
    private LocationClient locationClient;

    @Override
    public void onCreate() {
        // Initialize the binder
        mBinder = new LocalBinder<LocationService>(this);

        // Create a new location client using this class to handle callbacks
        locationClient = new LocationClient(this, this, this);
        // Test if Google Play Services is available
        servicesAvailable = areServicesConnected();
        // TODO launch activity or dialog if not available
        if (!servicesAvailable) {
            Log.d("LocationService", "servicesAvailable = false");
            super.onCreate();
        }
        // Connect the client
        locationClient.connect();

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d("LocationService","onDestroy");
        if (servicesAvailable && locationClient != null) { // TODO bug sometimes...
            locationClient.removeLocationUpdates(this);
            if(Debug.log) Log.d("LocationService","removed location updates");
            // Destroy the current location client
            locationClient = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return our interface binder
        return mBinder;
    }

    public Location getLocation() {
        return locationClient.getLastLocation();
    }

    /** Register the given LocationListener to receive location updates */
    public void register(LocationListener listener) {
        // The listener wish to monitor the location, add it to the list
        listenersLocation.add(listener);
        // If we have a previous known location
        if (location != null) {
            // Notify it immediatly with the current data
            listener.onLocationChanged(location);
        }
    }

    /** Unregister the given listener */
    public void unregister(LocationListener listener) {
        // Remove it from the list
        listenersLocation.remove(listener);
    }

    // TODO
    public void setMinimumRefreshRate(int milliseconds) {
        // TODO Auto-generated method stub
    }

    /** Notify each location listeners with the current ILocation value */
    private void notifyLocationListeners() {
        for (LocationListener listener : listenersLocation) {
            // For each listener, notify
            listener.onLocationChanged(location);
        }
    }

    /** Android callbacks */
    @Override
    public void onLocationChanged(Location newLocation) {
        // Update our Location
        location = newLocation;
        // Notify the listeners that a new location is available
        notifyLocationListeners();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects. If the error
         * has a resolution, try sending an Intent to start a Google Play
         * services activity that can resolve error.
         */
        // TODO inform the activities
        // if (connectionResult.hasResolution()) {
        // try {
        // // Start an Activity that tries to resolve the error
        // connectionResult.startResolutionForResult(
        // this,
        // CONNECTION_FAILURE_RESOLUTION_REQUEST);
        // /*
        // * Thrown if Google Play services canceled the original
        // * PendingIntent
        // */
        // } catch (IntentSender.SendIntentException e) {
        // // Log the error
        // e.printStackTrace();
        // }
        // } else {
        // /*
        // * If no resolution is available, display a dialog to the
        // * user with the error.
        // */
        // }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("LocationService", "onConnected services");
        // Request location updates with locationRequest settings
        locationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onDisconnected() {
        if(Debug.log) Log.d("LocationService","onDisconnected");
        // Destroy the current location client (we will start anew)
        locationClient = null;
        if(Debug.log) Log.d("LocationService","locationClient == null");

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
