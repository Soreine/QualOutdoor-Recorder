package com.qualoutdoor.recorder.location;

import android.location.Location;
import android.os.Bundle;

/**
 * An Android implementation of the ILocation interface
 * 
 * @author Gaborit Nicolas
 */
public class CustomLocation implements ILocation {

    /**
     * This bundle hold all this location data. We are using bundles because
     * they let us provide partial informations. Plus they are easily passed
     * between activities.
     */
    private Bundle locationBundle;

    /************ The bundle keys ****************/
    /** Stores the date of the sample. Holds a long. */
    public static final String TIME = "time";
    /** Stores the latitude value. Holds a double. */
    public static final String LATITUDE = "latitude";
    /** Stores the latitude value. Holds a double. */
    public static final String LONGITUDE = "longitude";
    /** Stores the accuracy of the location. Holds a float. */
    public static final String ACCURACY = "accuracy";

    /**
     * Create a custom location with the given location values.
     * 
     * @param location
     *            The location from which to take values
     */
    public CustomLocation(Location location) {
        // Initialize the bundle
        locationBundle = new Bundle();
        // Add the time
        locationBundle.putLong(TIME, location.getTime());
        // Add the latitude
        locationBundle.putDouble(LATITUDE, location.getLatitude());
        // Add the longitude
        locationBundle.putDouble(LONGITUDE, location.getLongitude());
        // Add the accuracy
        locationBundle.putFloat(ACCURACY, location.getAccuracy());
    }

    /** Return this as a bundle */
    public Bundle getBundle() {
        return this.locationBundle;
    }

    @Override
    public long getTime() {
        return locationBundle.getLong(TIME);
    }

    @Override
    public double getLatitude() {
        return locationBundle.getDouble(LATITUDE);
    }

    @Override
    public double getLongitude() {
        return locationBundle.getDouble(LONGITUDE);
    }

    @Override
    public float getAccuracy() {
        return locationBundle.getFloat(ACCURACY);
    }

}
