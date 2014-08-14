package com.qualoutdoor.recorder;

public class GlobalConstants {

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
    // Number of milliseconds in a second
    public static final int MILLIS_IN_SECOND = 1000;

    /**Strings identifiers of sending protocol to use*/
    public static final int SENDING_PROTOCOL_HTTP = 1;
    public static final int SENDING_PROTOCOL_FTP = 2;
    
    /**Archive file name*/
    public static final String ARCHIVE_NAME = "pendingFiles.zip";

}
