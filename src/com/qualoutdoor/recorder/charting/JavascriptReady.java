package com.qualoutdoor.recorder.charting;

import android.webkit.JavascriptInterface;

/**
 * This interface can be added to a WebView as a JavascriptInterface. Allow
 * Javascript to make a callback when the DOM is ready and Javascript has
 * initialised its variables.
 * 
 * @author Gaborit Nicolas
 */
public interface JavascriptReady {
    
    /** This is the name of the component made available in Javascript */
    public static String NAME = "JavascriptReady";
    
    /** To be called when the DOM and Javascript have initialised */
    @JavascriptInterface
    public void onDocumentReady();
}
