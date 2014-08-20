package com.qualoutdoor.recorder.location;

import com.qualoutdoor.recorder.ServiceProvider;

/**
 * Interface for contexts that can hand out a LocationService ServiceProvider
 * 
 * @author Gaborit Nicolas
 */
public interface LocationContext {

    /** Get a LocationService Provider */
    ServiceProvider<LocationService> getLocationServiceProvider();
}
