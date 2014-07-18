package com.qualoutdoor.recorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/** A generic fragment, to be used in place of a 'non implemented yet fragment'. */
public class GenericFragment extends Fragment {

	// The name argument key
	public static final String FRAGMENT_NAME = "fragment_name";

	/** The name of the generic fragment (and the name to display) */
	private CharSequence name = "Generic Fragment";

	public GenericFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the arguments passed
		Bundle args = getArguments();
		// Check that arguments were passed
		if (args != null) {
			// Check if we had a name passed as argument
			CharSequence nameArg = args.getCharSequence(FRAGMENT_NAME);
			if (nameArg != null) {
				this.name = nameArg;
			}
		}
	}

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
