package com.qualoutdoor.recorder.telephony;

/** This is an interface for accessing a cell information */
public interface ICellInfo {
    
    /** This cell represent an unknown type of cell */
    static final int CELL_UNKNOWN = 0;
    /** This cell represent a CDMA cell */
    static final int CELL_CDMA = 4;
    /** This cell represent a GSM cell */
    static final int CELL_GSM = 1;
    /** This cell represent a LTE cell */
    static final int CELL_LTE = 3;
    /** This cell represent a WCDMA cell */
    static final int CELL_WCDMA = 2;

    /** Get the cell type code. The code returned indicates the
     * technology associated with the cell. 
     * May return CELL_UNKNOWN, CELL_CDMA, CELL_GSM, CELL_LTE, CELL_WCDMA */
    int getCellType();

    /** Approximate time of this cell information in nanoseconds since boot */
    long getTimeStamp();
    
    /** True if this cell is registered to the network. */
    boolean isRegistered();
    
    /** Return an object that encapsulate the signal strength from this cell */
    ISignalStrength getSignalStrength();

     /** Get the cell identity.
     * @return The CID. Integer.MAX_VALUE if unknown or not GSM, WCDMA, LTE. */
    int getCid();

     /** Get the location area code.
     * @return The LAC. Integer.MAX_VALUE if unknown or not GSM, WCDMA. */
    int getLac();

     /** Get the Mobile Country Code.
     * @return The MCC. Integer.MAX_VALUE if unknown or not GSM, WCDMA, LTE. */
    int getMcc();

     /** Get the Mobile Network Code.
     * @return The MNC. Integer.MAX_VALUE if unknown or not GSM, WCDMA, LTE. */
    int getMnc();

     /** Get the primary scrambling code.
     * @return The CID. Integer.MAX_VALUE if unknown or not WCDMA. */
    int getPsc();
    
}