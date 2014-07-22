package com.qualoutdoor.recorder.telephony;

/** This is an interface for accessing a cell information */
public interface ICellInfo {
    /** Approximate time of this cell information in nanoseconds since boot */
    long getTimeStamp();
    
    /** True if this cell is registered to the network */
    boolean isRegistered();

    /** Others methods need to be added */
    void todo();
}
