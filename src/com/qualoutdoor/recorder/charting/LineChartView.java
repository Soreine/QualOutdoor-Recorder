package com.qualoutdoor.recorder.charting;

import android.content.Context;
import android.util.AttributeSet;

public class LineChartView extends HighChartView {

    /** The HTML file for line charts */
    private static final String HTML_FILE = "file:///android_asset/web/line-chart.html"; 
    
    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Load line chart HTML file
        loadUrl(HTML_FILE);
    }

}
