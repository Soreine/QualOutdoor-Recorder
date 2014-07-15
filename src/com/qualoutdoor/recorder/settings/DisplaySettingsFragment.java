package com.qualoutdoor.recorder.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

		// Set up the Start/Stop button behavior
		Button startStop = (Button) rootView
				.findViewById(R.id.settings_start_stop_button);
		// Set the onClick listener to call the Start Stop method
		startStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				actionStartStop(v);
			}
		});

		return rootView;
	}

	/** Start or stop sampling (for now only a notification) */
	private void actionStartStop(View v) {
		if (sampling) {
			NotificationCenter.dismissBackgroundSampling(getActivity());
			sampling = false;
		} else {
			NotificationCenter.notifyBackgroundSampling(getActivity());
			sampling = true;
		}
	}
}
