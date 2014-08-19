package com.qualoutdoor.recorder.location;

/**
 * This is an interface for accessing a location details
 * 
 * @author Gaborit Nicolas
 */
public interface ILocation {
    /** Return the UTC time of this data, in milliseconds since January 1, 1970 */
    long getTime();

    /** Get the latitude, in degrees */
    double getLatitude();

    /** Get the longitude, in degrees */
    double getLongitude();

    /** Get the estimated accuracy of this location, in meters */
    float getAccuracy();
}
