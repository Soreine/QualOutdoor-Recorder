package com.qualoutdoor.recorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/** A generic fragment, to be used in place of a 'non implemented yet fragment'. */
public class GenericFragment extends Fragment {

	// The key to retrieve the fragment state when restoring from a saved
	// instance Bundle.
	private final String FRAGMENT_NAME = "fragment_name";

	/** The name of the generic fragment (and the name to display) */
	private String name = "Generic Fragment";

	public GenericFragment() {
		super();
	}

	/** Create a GenericFragment called 'name' */
	public GenericFragment(String name) {
		super();
		this.name = name;
	}

	@Override
	/** Called when the activity will be destroyed and its state might be restored later. */
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save the name
		outState.putString(FRAGMENT_NAME, this.name);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Check if we are being restored from a previous state
		if (savedInstanceState != null) {
			// Restore the name of the fragment
			this.name = savedInstanceState.getString(FRAGMENT_NAME);
		}
	}

	/** Called on creation of the view */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout from the xml file.
		View rootView = inflater.inflate(R.layout.fragment_generic, container,
				false);

		// Get the TextView instance
		TextView textView = (TextView) (rootView
				.findViewById(R.id.generic_text));
		// Set the content of the TextView
		textView.setText(name);
		return rootView;
	}
}
