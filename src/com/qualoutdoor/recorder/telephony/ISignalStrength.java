package com.qualoutdoor.recorder.telephony;


/** This is an interface for accessing a signal strength information */
public interface ISignalStrength {
    /** Get the RSSI value as dBm */
    int getDbm();
    
    /** Get the signal level as an asu value.
     * LTE and CDMA values are between 0..97.
     * GSM and WCDMA values are between 0..31.
     * 99 is unknown. */
    int getAsuLevel();

}
