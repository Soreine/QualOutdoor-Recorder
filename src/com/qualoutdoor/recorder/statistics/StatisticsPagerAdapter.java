package com.qualoutdoor.recorder.statistics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.qualoutdoor.recorder.GenericFragment;
import com.qualoutdoor.recorder.charting.SignalStrengthPlotFragment;

/**
 * The pager adapter for the StatisticsFragment.
 * 
 * @author Gaborit Nicolasx
 * 
 */
public class StatisticsPagerAdapter extends FragmentPagerAdapter {

    /** The order of the fragments */
    private static final int CELL_INFO = 0;
    private static final int NEIGHBOR = 1;
    private static final int GRAPH = 2;
    private static final int SCRIPT_LOGS = 3;

    /** The list of the fragment names */
    private CharSequence[] fragmentTitles = {
            "Cell Infos", "Neighbor Cells", "Graph", "Script Logs"
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
        args.putCharSequence(GenericFragment.FRAGMENT_NAME, fragmentTitles[i]);

        // Switch on the fragment name
        switch (i) {
        case CELL_INFO:
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
