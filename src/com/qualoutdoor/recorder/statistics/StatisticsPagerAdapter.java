package com.qualoutdoor.recorder.statistics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.qualoutdoor.recorder.GenericFragment;
import com.qualoutdoor.recorder.charting.SignalStrengthPlotFragment;
import com.qualoutdoor.recorder.charting.WebFragment;

/**
 * The pager adapter for the StatisticsFragment.
 * 
 * We extend FragmentStatePagerAdapter, which will destroy and re-create
 * fragments as needed, saving and restoring their state in the process. This is
 * important to conserve memory and is a best practice when allowing navigation
 * between objects in a potentially large collection.
 * 
 * @author Gaborit Nicolas
 * 
 */
public class StatisticsPagerAdapter extends FragmentPagerAdapter {

    /** Position of the WebView fragment */
    private static final int WEB_VIEW = 0;
    /** Position of the neighboring cells fragment */
    private static final int NEIGHBOR = 1;
    /** Position of the graph fragment */
    private static final int GRAPH = 2;
    /** Position of the script logs fragment */
    private static final int SCRIPT_LOGS = 3;

    /** The list of the fragment titles */
    /*
     * TODO This is temporary hard coded. Should be reference in XML, see the
     * example in the HomePagerAdapter class
     */
    private CharSequence[] fragmentTitles = {
            "WebView", "Neighbor Cells", "Graph", "Script Logs"
    };

    public StatisticsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        // Initialize the resulting fragment
        Fragment result = new GenericFragment();

        // Initialize the arguments bundle
        Bundle args = new Bundle();
        args.putCharSequence(GenericFragment.FRAGMENT_TEXT, fragmentTitles[i]);

        // Switch on the fragment name
        switch (i) {
        case WEB_VIEW:
            result = new WebFragment();
            break;
        case NEIGHBOR:
            break;
        case GRAPH:
            result = new SignalStrengthPlotFragment();
            break;
        case SCRIPT_LOGS:
            break;
        }

        // Attach the arguments
        result.setArguments(args);

        return result;
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
