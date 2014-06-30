package com.qualoutdoor.recorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class DataDisplayActivity extends FragmentActivity implements
		SelectDisplayFragment.OnViewSelectedListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_display);

		// Check that the activity is using the "one fragment at a time" layout
		// version with the fragment_container FrameLayout
		if (findViewById(R.id.fragment_container) != null) {

			// Check if we're being restored from a previous state (in which
			// case the Fragment is already initialized)
			if (savedInstanceState != null) {
				// Do nothing, otherwise we could end up with overlapping
				// fragments
				return;
			}

			// Create a new DataMapFragment to be placed in the activity layout
			SelectDisplayFragment firstFragment = new SelectDisplayFragment();

			// In case this activity was started with special instructions from
			// an Intent, pass the Intent's extras to the fragment as arguments
			firstFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, firstFragment)
					.commit();
		}

	}

	@Override
	public void onViewSelected(int position) {
		// The user selected a view from the SelectDisplayFragment

		// Create the corresponding fragment
		Fragment newFragment = new Fragment();
		switch (position) {
		case 0: // Time view
			newFragment = new SignalStrengthPlotFragment();
			break;
		case 1: // Map view
			newFragment = new DataMapFragment();
			break;
		}

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		// Check if we are in a two-pane layout
		SelectDisplayFragment selectFragment = null;
//		(SelectDisplayFragment) getSupportFragmentManager()
//				.findFragmentById(R.id.select_display_fragment);
		if (selectFragment == null) {
			// We are in a single-pane layout

			// Replace whatever is in the fragment_container view with this
			// new fragment, and add the transaction to the back stack so the
			// user can navigate back
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		} else {
			// We are in a two-pane layout

			// Replace whatever is in the view_container view with this
			// new fragment, and add the transaction to the back stack so the
			// user can navigate back
//			transaction.replace(R.id.view_container, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}

	}
}
