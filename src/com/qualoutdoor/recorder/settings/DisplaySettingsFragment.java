package com.qualoutdoor.recorder.settings;

import java.util.Random;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.qualoutdoor.recorder.QualOutdoorApp;
import com.qualoutdoor.recorder.R;
import com.qualoutdoor.recorder.notifications.NotificationCenter;

/** A generic fragment, to be used in place of a 'non implemented yet fragment'. */
public class DisplaySettingsFragment extends Fragment {
	

	/** Indicate if sampling is running or not... Temporary */
	private static boolean sampling = false;

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

	/** Start or stop sampling artificially (currently only a notification) */
	private void actionStartStop(View view) {
		if (sampling) {
			NotificationCenter.dismissBackgroundSampling(getActivity());
			sampling = false;
		} else {
			NotificationCenter.notifyBackgroundSampling(getActivity());
			sampling = true;
		}
	}

}
