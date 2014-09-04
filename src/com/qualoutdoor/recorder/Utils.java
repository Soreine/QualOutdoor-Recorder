package com.qualoutdoor.recorder;

/**
 * A static class containing various utilitary methods
 * @author Gaborit Nicolas
 *
 */
public final class Utils {

    /**
     * Capitalize a string by setting the first character to uppercase
     * 
     * @param s
     *            The string to capitalize
     * @return The capitalized string
     */
    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    
}
