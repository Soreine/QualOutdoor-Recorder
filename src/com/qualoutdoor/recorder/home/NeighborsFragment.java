package com.qualoutdoor.recorder.home;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.qualoutdoor.recorder.R;
import com.qualoutdoor.recorder.IServiceListener;
import com.qualoutdoor.recorder.ServiceProvider;
import com.qualoutdoor.recorder.ServiceProvider.ServiceNotBoundException;
import com.qualoutdoor.recorder.telephony.ICellInfo;
import com.qualoutdoor.recorder.telephony.TelephonyContext;
import com.qualoutdoor.recorder.telephony.TelephonyListener;
import com.qualoutdoor.recorder.telephony.TelephonyService;

/**
 * This fragment displays the list of the visible cells. Its parent activity
 * must implements the interface TelephonyContext.
 * 
 * @author Gaborit Nicolas
 */
public class NeighborsFragment extends Fragment {

    /** The events monitored by the Telephony Listener */
    private static final int events = TelephonyListener.LISTEN_CELL_INFO;

    /**
     * The Telephony Listener, which defines the behavior against telephony
     * state changes
     */
    private TelephonyListener telListener = new TelephonyListener() {

        @Override
        public void onCellInfoChanged(List<ICellInfo> cellInfos) {
            // Update the cell info list adapter
            listAdapter.updateDataSet(cellInfos);
        }
    };

    /** The TelephonyService Provider given by the activity */
    private ServiceProvider<TelephonyService> telephonyService;
    /**
     * The service listener defines the behavior when the service becomes
     * available
     */
    private IServiceListener<TelephonyService> telServiceListener = new IServiceListener<TelephonyService>() {
        @Override
        public void onServiceAvailable(TelephonyService service) {
            // Register the telephony listener
            telephonyService.getService().listen(telListener, events);
        }
    };

    /** The cell info list adapter */
    private CellInfoListAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Initialize the cell info list adapter with an empty cell info list
        listAdapter = new CellInfoListAdapter(getActivity(),
                new ArrayList<ICellInfo>());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // This cast makes sure that the container activity has implemented
            // TelephonyContext
            TelephonyContext telephonyContext = (TelephonyContext) getActivity();

            // Retrieve the service connection
            telephonyService = telephonyContext.getTelephonyServiceProvider();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + TelephonyContext.class.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the list view
        ListView view = (ListView) inflater.inflate(
                R.layout.fragment_neighbors, container, false);
        // Set the adapter for the list
        view.setAdapter(listAdapter);

        return view;
    }

    @Override
    public void onResume() {
        // Tell we want to be informed when services become available
        telephonyService.register(telServiceListener);
        super.onStart();
    }

    @Override
    public void onPause() {
        // If needed unregister our telephony listener
        try {
            telephonyService.getService().listen(telListener,
                    TelephonyListener.LISTEN_NONE);
        } catch (ServiceNotBoundException e) {}
        // Unregister the services listeners
        telephonyService.unregister(telServiceListener);
        super.onPause();
    }

}