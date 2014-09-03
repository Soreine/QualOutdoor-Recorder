package com.qualoutdoor.recorder.charting;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.qualoutdoor.recorder.R;

/**
 * This view is used to display an HighChart chart as an Android View. It wrap a
 * WebView containing a Javascript HighChart.
 * 
 * @author Gaborit Nicolas
 * 
 */
public class HighChartView extends WebView {

    /** The Javascript name of the chart variable */
    private static CharSequence CHART = "chart";

    /** The title string */
    private CharSequence chartTitle = null;
    /** The title on the x-axis */
    private CharSequence xAxisTitle = null;
    /** The title on the y-axis */
    private CharSequence yAxisTitle = null;
    /** The label to add to each value on the x-axis */
    private CharSequence xAxisLabelUnit = null;
    /** The label to add to each value on the y-axis */
    private CharSequence yAxisLabelUnit = null;

    /** Defines what to do when */
    private final JavascriptReady jsReady = new JavascriptReady() {
        @Override
        public void onDocumentReady() {
            // Javascript can now be executed on the page
            // So initialize the chart (and do it on UI thread)
            HighChartView.this.post(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            });
        }
    };

    public HighChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Get the styled attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.HighChartView, 0, 0);

        // Parse them
        try {
            chartTitle = a.getString(R.styleable.HighChartView_chartTitle);
            xAxisTitle = a.getString(R.styleable.HighChartView_xAxisTitle);
            yAxisTitle = a.getString(R.styleable.HighChartView_yAxisTitle);
            xAxisLabelUnit = a
                    .getString(R.styleable.HighChartView_xAxisLabelUnit);
            yAxisLabelUnit = a
                    .getString(R.styleable.HighChartView_yAxisLabelUnit);
        } finally {
            // In any case release the array
            a.recycle();
        }

        // Add our callback for when javascript has initialized
        this.addJavascriptInterface(jsReady, JavascriptReady.NAME);
    }

    /** Initialize the chart with our attribute values */
    private void init() {
        // The javascript code to execute
        String srcJS = "";

        // Set the title
        if (chartTitle == null)
            // Disable the title
            srcJS += CHART + ".setTitle({text: null});";
        else
            srcJS += setTitleJS(chartTitle);

        // Set the X title
        if (xAxisTitle != null)
            // Disable
            srcJS += CHART + ".xAxis[0].setTitle({text:null});";
        else
            srcJS += CHART + ".xAxis[0].setTitle({text:'" + xAxisTitle + "'});";
        // Set the X title
        if (yAxisTitle == null)
            // Disable
            srcJS += CHART + ".yAxis[0].setTitle({text:null});";
        else
            srcJS += CHART + ".yAxis[0].setTitle({text:" + yAxisTitle + "});";
        
        //TODO
        
        // Execute the javascript
        execJS(srcJS);
        
    }

    /**
     * Execute the given script in the WebView wrapped by this HighChartView
     * 
     * @param srcJS
     *            The Javascript source to be executed
     */
    private void execJS(CharSequence srcJS) {
        this.loadUrl("javascript:" + srcJS);
    }

    /** Set the title of the chart */
    public void setTitle(CharSequence title) {
        // Set the title value
        chartTitle = title;
        // Set the title in javascript
        execJS(setTitleJS(title));
    }

    /**
     * Generate the code for setting the title
     * 
     * @param title
     * @return The JS code setting the title
     */
    private CharSequence setTitleJS(CharSequence title) {
        return CHART + ".setTitle({text:'" + title + "'});";
    }
}
