package com.qualoutdoor.recorder.charting;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
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


    /** The chart title */
    private String chartTitle;
    /** The chart subtitle */
    // private String chartSubTitle; // TODO
    /** The title on the x-axis */
    private String xAxisTitle;
    /** The title on the y-axis */
    private String yAxisTitle;
    /** The label to add to each value on the x-axis */
    private String xAxisLabelUnit;
    /** The label to add to each value on the y-axis */
    private String yAxisLabelUnit;
    
    /** Called by javascript when the document is ready */
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
    

    public HighChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Get the styled attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.HighChartView, 0, 0);

        // Parse them
        try {
            // For each value, wrap it into the good Object
            chartTitle = asJsValue(a
                    .getString(R.styleable.HighChartView_chartTitle));
            xAxisTitle = asJsValue(a
                    .getString(R.styleable.HighChartView_xAxisTitle));
            yAxisTitle = asJsValue(a
                    .getString(R.styleable.HighChartView_yAxisTitle));
            xAxisLabelUnit = asJsValue(a
                    .getString(R.styleable.HighChartView_xAxisLabelUnit));
            yAxisLabelUnit = asJsValue(a
                    .getString(R.styleable.HighChartView_yAxisLabelUnit));
        } finally {
            // In any case release the array
            a.recycle();
        }
        
        // Add our callback for when javascript has initialized
        this.addJavascriptInterface(jsReady, JavascriptReady.NAME);
    }

    /** Initialize the chart with our attribute values */
    public void init() {
        // The javascript code to execute
        String srcJS = "";

        // Create the config JSONOBject
        String jsonConfig = "";

        jsonConfig += "{" + "title:{text:" + chartTitle + "}," + "xAxis:{"
                + "title:{text:" + xAxisTitle + "}," + "labels:{format:"
                + xAxisLabelUnit + "}," + "}," + "yAxis:{" + "title:{text:"
                + yAxisTitle + "}," + "labels:{format:" + yAxisLabelUnit + "},"
                + "}," + "}";

        Log.d("HighChartView", jsonConfig);

        // Add this config to the chart
        srcJS += "initConfig(" + jsonConfig + ")";

        // Execute the javascript
        execJS(srcJS);
    }

    /**
     * Execute the given script in the WebView wrapped by this HighChartView
     * 
     * @param srcJS
     *            The Javascript source to be executed
     */
    public void execJS(CharSequence srcJS) {
        this.loadUrl("javascript:" + srcJS);
    }

    /** If null return "null" else return "\"value\"" */
    private String asJsValue(String value) {
        if (value == null)
            return "null";
        else
            return "\"" + value + "\"";
    }

}
