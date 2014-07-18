package com.qualoutdoor.recorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.qualoutdoor.recorder.map.DataMapFragment;
import com.qualoutdoor.recorder.scripts.ScriptListFragment;
import com.qualoutdoor.recorder.statistics.StatisticsFragment;

/**
 * This class define the order and the association between items in the
 * Navigation Drawer and their corresponding fragments
 */
public class NavigationDrawerBehavior {
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
			result = new OverviewFragment();
			break;
		case MAP:
			result = new DataMapFragment();
			break;
		case STATISTICS:
			result = new StatisticsFragment();
			break;
		case SCRIPTS:
			// Create a Generic Fragment
			result = new ScriptListFragment();
			break;
		}

		// Give the arguments bundle to the fragment
		result.setArguments(args);

		return result;
	}

}
