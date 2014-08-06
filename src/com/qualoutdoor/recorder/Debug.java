package com.qualoutdoor.recorder;

import android.util.Log;


/** Allows to activate debug modes*/
public class Debug {
    public static final boolean debug = true;
    
    public static final boolean log = true;
    
    public static void log(String tag, String msg) {
        Log.d(tag, msg);
    }
    
}
