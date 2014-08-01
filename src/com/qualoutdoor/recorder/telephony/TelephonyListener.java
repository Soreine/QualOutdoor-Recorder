package com.qualoutdoor.recorder.telephony;

import java.util.List;

import com.qualoutdoor.recorder.location.ILocation;

/**
 * A listener class for monitoring changes in specific telephony states.
 * Override the methods for the state that you want to receive updates for, and
 * pass your TelephonyListener object, along with bitwise-or of the LISTEN_xxx
 * flags to ITelephony.listen()
 */
public class TelephonyListener {

    /*********************************************
     * Theses constant are used as bitwise mask, hence the power of two.
     *********************************************/
    /** Listen for changes to the device call state. */
    public static final int LISTEN_CALL_STATE = 8;
    /** Listen for changes to observed cell info. */
    public static final int LISTEN_CELL_INFO = 1024;
    /** Listen for changes to the data connection state (cellular). */
    public static final int LISTEN_DATA_STATE = 64; 
    /** Listen for changes to the device location */
    public static final int LISTEN_LOCATION = 2048;
    /** Stop listening for updates. */
    public static final int LISTEN_NONE = 0;
    /** Listen for changes to the network signal strengths (cellular). */
    public static final int LISTEN_SIGNAL_STRENGTHS = 256;

    /** Constructor */
    public TelephonyListener() {}

    /** Callback invoked when device call state changes. */
    public void onCallStateChanged(int state, String incomingNumber) {}

    /**
     * Callback invoked when a observed cell info has changed, or new cells have
     * been added or removed.
     */
    public void onCellInfoChanged(List<ICellInfo> cellInfo) {}

    /** Callback invoked when network type or data connection state change. */
    public void onDataStateChanged(int state, int networkType) {}

    /** Callback invoked when network signal strengths changes. */
    public void onSignalStrengthsChanged(ISignalStrength signalStrength) {}

}