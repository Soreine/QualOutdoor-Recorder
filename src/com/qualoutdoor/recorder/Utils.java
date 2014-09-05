package com.qualoutdoor.recorder;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

/**
 * A static class containing various utilitary methods
 * 
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

    /**
     * Convert a Bundle into a JSONObject
     * 
     * @param bundle
     *            The bundle to convert.
     */
    public static JSONObject bundleToJSON(Bundle bundle) {
        // The resulting JSONObject
        JSONObject result = new JSONObject();

        // Find all the keys
        Set<String> keys = bundle.keySet();

        // Add each key value into the JSON
        for (String key : keys) {
            try {
                // The associated value
                Object value = bundle.get(key);
                // Check if the value is a Bundle too
                if (value instanceof Bundle) {
                    // Convert as a JSONObject
                    result.put(key, bundleToJSON((Bundle) value));
                } else {
                    // Add the key:value pair
                    result.put(key, value);
                }
                // If API 19 or higher, this call can handle Collection and Map
                // type
                // result.put(key, JSONObject.wrap(infoBundle.get(key)));
            } catch (JSONException e) {
                // Ignore this key
            }
        }

        return result;
    }
}
