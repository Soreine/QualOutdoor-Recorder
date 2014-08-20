package com.qualoutdoor.recorder.location;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.qualoutdoor.recorder.Debug;
import com.qualoutdoor.recorder.LocalBinder;
import com.qualoutdoor.recorder.R;

/**
 * This service is allows to acces location data, it uses a Google Play Services
 * LocationClient in order to receive location update. An app component can bind
 * to it any time in order to monitor location.
 * 
 * @author Gaborit Nicolas
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
    private LocationRequest locationRequest;
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

    /** The current location update interval */
    private int updateInterval;

    /** Indicate if the client is connected */
    private boolean clientConnected = false;

    @Override
    public void onCreate() {
        // Initialize the binder
        mBinder = new LocalBinder<LocationService>(this);

        // Initialize the location update interval
        updateInterval = DEFAULT_LOCATION_UPDATE_INTERVAL;

        // Create a new location client using this class to handle callbacks
        locationClient = new LocationClient(this, this, this);
        // Test if Google Play Services is available
        servicesAvailable = areServicesConnected();
        // If not available
        if (!servicesAvailable) {
            // Toast something useful
            Toast.makeText(this,
                    getString(R.string.error_location_services_unavailable),
                    Toast.LENGTH_SHORT).show();
            Log.e("LocationService", "servicesAvailable = false");
        } else {
            // Connect the client
            locationClient.connect();
        }
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d("LocationService", "onDestroy");
        if (servicesAvailable && locationClient != null) {
            locationClient.removeLocationUpdates(this);
            if (Debug.log)
                Log.d("LocationService", "removed location updates");
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

    /** Return the last known location */
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

    /** Reduce the refresh rate to match the given time in milliseconds */
    public void setMinimumRefreshRate(int milliseconds) {
        updateInterval = Math.min(milliseconds, updateInterval);
        // Update the location request
        updateRequest();

    }

    /** Update the location request */
    private void updateRequest() {
        // Update the interval
        locationRequest.setInterval(updateInterval);
        // Request again if already running
        if (clientConnected && locationClient != null && servicesAvailable) {
            locationClient.requestLocationUpdates(locationRequest, this);
        }
    }

    /** Notify each location listeners with the current ILocation value */
    private void notifyLocationListeners() {
        for (LocationListener listener : listenersLocation) {
            // For each listener, notify
            listener.onLocationChanged(location);
        }
    }

    /* Android callbacks */
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
         * has a resolution, it is possible to try sending an Intent to start a
         * Google Play services activity that can resolve error.
         */
        /*
         * But we are not an activity, so just inform the user x(
         */
        Toast.makeText(this,
                getString(R.string.error_location_on_connection_failed),
                Toast.LENGTH_SHORT).show();
        Log.e("LocationService", "onConnectionFailed");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("LocationService", "onConnected services");
        // Request location updates with locationRequest settings
        locationClient.requestLocationUpdates(locationRequest, this);
        // Indicate we are connected
        clientConnected = true;
    }

    @Override
    public void onDisconnected() {
        if (Debug.log)
            Log.d("LocationService", "onDisconnected");
        // Destroy the current location client (we will start anew)
        locationClient = null;
        // We are no longer connected
        clientConnected = false;
        if (Debug.log)
            Log.d("LocationService", "locationClient == null");

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
