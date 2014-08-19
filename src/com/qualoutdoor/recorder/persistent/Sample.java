package com.qualoutdoor.recorder.persistent;

import android.util.SparseArray;

/**
 * A class that encapsulate the data required for inserting a sample in the
 * database
 * 
 * @author Gaborit Nicolas
 */
public class Sample {
    /** The measure context of the sample */
    public MeasureContext measureContext;
    /** The metrics that have been sampled and their values */
    public SparseArray<String> data;
    /** Location of the sample */
    public double latitude;
    public double longitude;

    public Sample(MeasureContext measureContext, SparseArray<String> data,
            double latitude, double longitude) {
        this.measureContext = measureContext;
        this.data = data;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
