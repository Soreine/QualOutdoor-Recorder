package com.qualoutdoor.recorder.statistics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.qualoutdoor.recorder.GenericFragment;

public class StatisticsPagerAdapter extends FragmentPagerAdapter {

	/** The list of the fragment names */
	private CharSequence[] fragmentTitles = {"Cell Infos", "Neighbor Cells", "Graph", "Script Logs"};
	
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
		args.putCharSequence(GenericFragment.FRAGMENT_NAME, fragmentTitles[i]);
		// Attach the arguments
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int getCount() {
		// For this contrived example, we have a 10-object collection.
		return fragmentTitles.length;
	}

	@Override
	public CharSequence getPageTitle(int i) {
		return fragmentTitles[i];
	}

}
