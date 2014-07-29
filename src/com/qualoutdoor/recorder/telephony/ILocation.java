package com.qualoutdoor.recorder.telephony;

/** This is an interface for accessing a location details */
public interface ILocation {
	/** Return the UTC time of this data, in milliseconds since January 1, 1970 */
	long getTime();

	/** Get the latitude, in degrees */
	double getAltitude();

	/** Get the longitude, in degrees */
	double getLongitude();

	/** Get the estimated accuracy of this location, in meters */
	float getAccuracy();
}