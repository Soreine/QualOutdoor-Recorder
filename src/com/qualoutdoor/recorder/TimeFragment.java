package com.qualoutdoor.recorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TimeFragment extends Fragment {

	// Called when the fragment has to instantiate its own view
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the view from the xml layout file
		View rootView = inflater.inflate(R.layout.fragment_time, container,
				false);
		return rootView;
	}
}