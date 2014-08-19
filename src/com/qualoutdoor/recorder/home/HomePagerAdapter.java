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

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        // Initialize the resulting fragment
        Fragment result = new GenericFragment();

        // Initialize the arguments bundle
        Bundle args = new Bundle();

        // Load the ressources
        Resources res = Resources.getSystem();

        // The title string
        String title = "";

        // Switch on the fragment name
        switch (i) {
        case LOCATIONS:
            result = new LocationFragment();
            title = res.getString(R.string.title_location);
            break;
        case NETWORK:
            result = new NetworkFragment();
            title = res.getString(R.string.title_network);
            break;
        case NEIGHBORS:
            result = new NeighborsFragment();
            title = res.getString(R.string.title_neighbors);
            break;
        }

        // Attach the arguments
        result.setArguments(args);
        // Set the title
        args.putCharSequence(GenericFragment.FRAGMENT_NAME, title);

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
