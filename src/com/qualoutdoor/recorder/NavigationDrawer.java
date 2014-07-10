package com.qualoutdoor.recorder;

import com.qualoutdoor.recorder.statistics.StatisticsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * This class define the order and the association between items in the
 * Navigation Drawer and their corresponding fragments
 */
public class NavigationDrawer {
	// / The items positions
	public static final int OVERVIEW = 0;
	public static final int MAP = 1;
	public static final int STATISTICS = 2;
	public static final int SCRIPTS = 3;

	/** Get the fragment corresponding to the given item */
	public static Fragment getFragment(int itemPosition) {
		// The fragment that will be returned
		Fragment result = null;

		// Create the arguments bundle
		Bundle args = new Bundle();

		// Assign the correct fragment depending on the item position
		switch (itemPosition) {
		case OVERVIEW:
			// Create a Generic Fragment
			result = new GenericFragment();
			// Add the name argument
			args.putCharSequence(GenericFragment.FRAGMENT_NAME, "Overview");
			break;
		case MAP:
			// Create a Generic Fragment
			result = new GenericFragment();
			// Add the name argument
			args.putCharSequence(GenericFragment.FRAGMENT_NAME, "Map");
			break;
		case STATISTICS:
			result = new StatisticsFragment();
			break;
		case SCRIPTS:
			// Create a Generic Fragment
			result = new GenericFragment();
			// Add the name argument
			args.putCharSequence(GenericFragment.FRAGMENT_NAME, "Scripts");
			break;
		}

		// Give the arguments bundle to the fragment
		result.setArguments(args);

		Log.d("NavigationDrawer", "Fragment " + itemPosition + " created");
		return result;
	}

}
