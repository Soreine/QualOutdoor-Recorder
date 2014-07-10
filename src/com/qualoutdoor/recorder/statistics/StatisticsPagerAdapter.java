package com.qualoutdoor.recorder.statistics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.qualoutdoor.recorder.GenericFragment;

public class StatisticsPagerAdapter extends FragmentPagerAdapter {

	public StatisticsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		// Create a generic fragment
		Fragment fragment = new GenericFragment();
		// Create the arguments
		Bundle args = new Bundle();
		// Add the name argument
		args.putCharSequence(GenericFragment.FRAGMENT_NAME, "Fragment " + i);
		
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
