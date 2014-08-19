package com.qualoutdoor.recorder.telephony;

import java.util.List;

/**
 * This is an interface for a service that can provide information about network
 * state, location, phone state. It is quite similar to the TelephonyManager
 * class from the Android APIs.
 * 
 * @author Gaborit Nicolas
 */
public interface ITelephony {

    /** Device call state: No activity. */
    static final int CALL_STATE_IDLE = 0;
    /** Device call state: Off-hook. */
    static final int CALL_STATE_OFFHOOK = 2;
    /** Device call state: Ringing. */
    static final int CALL_STATE_RINGING = 1;

    /** Data connection state: Connected. */
    static final int DATA_CONNECTED = 2;
    /** Data connection state: Currently setting up a data connection. */
    static final int DATA_CONNECTING = 1;
    /** Data connection state: Disconnected. */
    static final int DATA_DISCONNECTED = 0;
    /** Data connection state: Suspended. */
    static final int DATA_SUSPENDED = 3;

    // / The values of the network type code are taken from
    // android.telephony.TelephonyManager
    /** Network type is unknown */
    static final int NETWORK_TYPE_UNKNOWN = 0;
    /** Current network is GPRS */
    static final int NETWORK_TYPE_GPRS = 1;
    /** Current network is EDGE */
    static final int NETWORK_TYPE_EDGE = 2;
    /** Current network is UMTS */
    static final int NETWORK_TYPE_UMTS = 3;
    /** Current network is CDMA: Either IS95A or IS95B */
    static final int NETWORK_TYPE_CDMA = 4;
    /** Current network is EVDO revision 0 */
    static final int NETWORK_TYPE_EVDO_0 = 5;
    /** Current network is EVDO revision A */
    static final int NETWORK_TYPE_EVDO_A = 6;
    /** Current network is 1xRTT */
    static final int NETWORK_TYPE_1xRTT = 7;
    /** Current network is HSDPA */
    static final int NETWORK_TYPE_HSDPA = 8;
    /** Current network is HSUPA */
    static final int NETWORK_TYPE_HSUPA = 9;
    /** Current network is HSPA */
    static final int NETWORK_TYPE_HSPA = 10;
    /** Current network is iDen */
    static final int NETWORK_TYPE_IDEN = 11;
    /** Current network is EVDO revision B */
    static final int NETWORK_TYPE_EVDO_B = 12;
    /** Current network is LTE */
    static final int NETWORK_TYPE_LTE = 13;
    /** Current network is eHRPD */
    static final int NETWORK_TYPE_EHRPD = 14;
    /** Current network is HSPA+ */
    static final int NETWORK_TYPE_HSPAP = 15;

    /**
     * Returns all the observed cell information including primary and
     * neighboring cells.
     */
    List<ICellInfo> getAllCellInfo();

    /** Returns a constant indicating the call state on the device. */
    int getCallState();

    /**
     * Returns a constant indicating the data connection state on the device.
     */
    int getDataState();

    /**
     * Returns a constant indicating the network type for the current data
     * connection.
     */
    int getNetworkType();

    /** Return the signal strength of the primary cell */
    ISignalStrength getSignalStrength();

    /**
     * Register a listener object to receive notification concerning the
     * specified events type
     */
    void listen(TelephonyListener listener, int events);

    /**
     * Set the minimum refresh rate for non event-driven data. These data can't
     * be monitored through a listener, so they need to be refreshed manually by
     * requesting the data provider at a fixed rate.
     */
    void setMinimumRefreshRate(int milliseconds);
}