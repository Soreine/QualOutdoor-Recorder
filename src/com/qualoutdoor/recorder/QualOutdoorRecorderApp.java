package com.qualoutdoor.recorder;

import android.app.Application;
import android.content.res.Resources;

/**
 * Extending Application in order to hold some global constants that can't be
 * stored in the XML files, and giving global access to application resources.
 * 
 * It is now possible to access the resources easily from a static context as follows :
 * 
 * ~~~
 * static Resources res = QualOutdoorRecorderApp.getAppResources();
 * ~~~
 * 
 * @author Gaborit Nicolas
 */
public class QualOutdoorRecorderApp extends Application {

    /** Store a reference to this application for global access */
    private static QualOutdoorRecorderApp thisApp = null;

    public static final int user = 11;
    public static final int group = 22;

    /** The metrics identifiers in the database */
    public static final int FIELD_CELL_ID = 1;
    public static final int FIELD_SIGNAL_STRENGTH = 2;
    public static final int FIELD_CALL = 3;
    public static final int FIELD_UPLOAD = 4;
    public static final int FIELD_DOWNLOAD = 5;

    /** Server related constants */
    public static final String URL_SERVER_HTTP = "http://192.168.0.4:8080/upload";
    public static final String URL_SERVER_FTP = "192.168.0.4";
    /** Number of milliseconds in a second */
    public static final int MILLIS_IN_SECOND = 1000;

    /** Strings identifiers of sending protocol to use */
    public static final int UPLOAD_PROTOCOL_HTTP = 1;
    public static final int UPLOAD_PROTOCOL_FTP = 2;

    /** Archive file name */
    public static final String ARCHIVE_NAME = "pendingFiles";

    @Override
    public void onCreate() {
        // Initialize this application reference
        thisApp = this;
    }

    /**
     * Provide a global access to the application resources
     * 
     * @return The resources relative to the application context
     */
    public static Resources getAppResources() {
        return thisApp.getApplicationContext().getResources();
    }
}
