package com.qualoutdoor.recorder;

import com.qualoutdoor.recorder.statistics.StatisticsFragment;

import android.support.v4.app.Fragment;
import android.util.Log;

public class NavigationDrawer {
	public static final int OVERVIEW = 0;
	public static final int MAP = 1;
	public static final int STATISTICS = 2;
	public static final int SCRIPTS = 3;

	public static Fragment getFragment(int position) {
		Fragment result = null;
		switch (position) {
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
		Log.d("NavigationDrawer", "Fragment " + position + " created");
		return result;
	}

}
