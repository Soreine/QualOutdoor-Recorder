package com.qualoutdoor.recorder.telephony;

import com.qualoutdoor.recorder.ServiceProvider;
import com.qualoutdoor.recorder.telephony.TelephonyService;

/**
 * Interface for context that can hand out a TelephonyService ServiceProvider
 * 
 * @author Gaborit Nicolas
 */
public interface TelephonyContext {

    /** Get a TelephonyService Provider */
    ServiceProvider<TelephonyService> getTelephonyServiceProvider();
}
