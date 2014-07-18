package com.qualoutdoor.recorder.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qualoutdoor.recorder.R;

/** This fragment give access to the different settings sub-categories */
public class DisplaySettingsFragment extends Fragment {

	public DisplaySettingsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout from the xml file.
		View rootView = inflater.inflate(R.layout.fragment_display_settings,
				container, false);

		return rootView;
	}

}
