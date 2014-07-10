package com.qualoutdoor.recorder;

import com.qualoutdoor.recorder.statistics.StatisticsFragment;

import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * This class define the order and the association between items in the
 * Navigation Drawer and their corresponding fragments
 */
public class NavigationDrawer {
	/// The items positions
	public static final int OVERVIEW = 0;
	public static final int MAP = 1;
	public static final int STATISTICS = 2;
	public static final int SCRIPTS = 3;

	/** Get the fragment corresponding to the given item */
	public static Fragment getFragment(int itemPosition) {
		// The fragment that will be returned
		Fragment result = null;
		
		// Assign the correct fragment depending on the item position
		switch (itemPosition) {
		case OVERVIEW:
			result = new GenericFragment("Overview");
			break;
		case MAP:
			result = new GenericFragment("Map");
			break;
		case STATISTICS:
			result = new StatisticsFragment();
			break;
		case SCRIPTS:
			result = new GenericFragment("Scripts");
			break;
		}
		Log.d("NavigationDrawer", "Fragment " + itemPosition + " created");

		return result;
	}

}
