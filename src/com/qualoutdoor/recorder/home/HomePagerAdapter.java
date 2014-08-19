package com.qualoutdoor.recorder.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.qualoutdoor.recorder.GenericFragment;

/**
 * This is the pager adapter for the HomeFragment
 * 
 * @author Gaborit Nicolas
 * 
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    /** The order of the fragments */
    public static final int LOCATIONS = 0;
    public static final int NETWORK = 1;
    public static final int NEIGHBORS = 2;

    // TODO put in xml
    /** The list of the fragment names */
    private CharSequence[] fragmentTitles = {
            "Locations", "Network", "Neighbors"
    };

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        // Initialize the resulting fragment
        Fragment result = new GenericFragment();

        // Initialize the arguments bundle
        Bundle args = new Bundle();
        args.putCharSequence(GenericFragment.FRAGMENT_NAME, fragmentTitles[i]);

        // Switch on the fragment name
        switch (i) {
        case LOCATIONS:
            result = new LocationFragment();
            break;
        case NETWORK:
            result = new NetworkFragment();
            break;
        case NEIGHBORS:
            result = new NeighborsFragment();
            break;
        }

        // Attach the arguments
        result.setArguments(args);

        return result;
    }

    @Override
    public int getCount() {
        return fragmentTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int i) {
        return fragmentTitles[i];
    }

}
