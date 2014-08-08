package com.qualoutdoor.recorder.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.qualoutdoor.recorder.R;

/** This fragment give access to the different settings sub-categories */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from the xml ressource
        addPreferencesFromResource(R.xml.preferences);
    }

}
