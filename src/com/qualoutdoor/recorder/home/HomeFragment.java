package com.qualoutdoor.recorder.home;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qualoutdoor.recorder.R;

/**
 * This is the main fragment of the application. The first displayed inside the
 * MainActivity.
 * 
 * @author Gaborit Nicolas
 */
public class HomeFragment extends Fragment {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments representing each object in a collection. We use a
     * {@link android.support.v4.app.FragmentStatePagerAdapter} derivative,
     * which will destroy and re-create fragments as needed, saving and
     * restoring their state in the process. This is important to conserve
     * memory and is a best practice when allowing navigation between objects in
     * a potentially large collection.
     */
    private HomePagerAdapter homePagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the
     * object collection.
     */
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an adapter that when requested, will return a fragment
        // representing an object in
        // the collection.
        //
        // ViewPager and its adapters use support library fragments, so we must
        // use getSupportFragmentManager.
        homePagerAdapter = new HomePagerAdapter(getChildFragmentManager());

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Set up the ViewPager
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        // Setting the limit of page cached to maximum (we do this as long as we
        // do not have much complex views)
        mViewPager.setOffscreenPageLimit(3); // Up to 3 + 1 + 3 pages will be
                                             // kept active at once
        // Attaching the adapter
        mViewPager.setAdapter(homePagerAdapter);

        // Select the middle fragment on start
        mViewPager.setCurrentItem(HomePagerAdapter.NETWORK);
        return view;
    }

}
