package com.qualoutdoor.recorder.home;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.qualoutdoor.recorder.GenericFragment;
import com.qualoutdoor.recorder.R;

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
    /** Number of fragments listed */
    private static CharSequence[] fragmentTitles = Resources.getSystem()
            .getStringArray(R.array.home_pager_titles);

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        // Initialize the resulting fragment
        Fragment result = new GenericFragment();

        // Initialize the arguments bundle
        Bundle args = new Bundle();
        // Set the title
        args.putCharSequence(GenericFragment.FRAGMENT_NAME, fragmentTitles[i]);

        // Switch on the fragment correct fragment
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
