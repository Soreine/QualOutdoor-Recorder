package com.qualoutdoor.recorder.charting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.qualoutdoor.recorder.R;

@SuppressLint("SetJavaScriptEnabled")
public class WebFragment extends Fragment {

    /** Reference to the web view used in this fragment */
    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the WebView from the xml layout file
        webView = (WebView) inflater.inflate(R.layout.fragment_web, container,
                false);

        WebSettings settings = webView.getSettings();
        // Enable Javascript
        settings.setJavaScriptEnabled(true);
        // Disable access to files outside of android_asset and android_res
        settings.setAllowFileAccess(false);
        // Allow JavaScript running in the context of a file scheme URL to
        // access content from any origin (solve same origin policy violation
        // but dangerous if we are accessing remote data)
        settings.setAllowFileAccessFromFileURLs(true);


        // Load local file
        webView.loadUrl("file:///android_asset/web/highchart.html");

        return webView;
    }
}
