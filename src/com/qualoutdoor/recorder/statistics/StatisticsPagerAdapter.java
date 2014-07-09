package com.qualoutdoor.recorder.statistics;

import com.qualoutdoor.recorder.GenericFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class StatisticsPagerAdapter extends FragmentPagerAdapter {

	public StatisticsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = new GenericFragment("Fragment " + i);
		return fragment;
	}

	@Override
	public int getCount() {
		// For this contrived example, we have a 10-object collection.
		return 10;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "Fragment " + (position + 1);
	}

}
