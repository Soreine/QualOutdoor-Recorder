package com.qualoutdoor.recorder.charting;

import android.content.Context;
import android.util.AttributeSet;

public class BarChartView extends HighChartView {

    /** The HTML file for bar charts */
    private static final String HTML_FILE = "file:///android_asset/web/bar-chart.html"; 
    
    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Load bar chart HTML file
        loadUrl(HTML_FILE);
    }
}
