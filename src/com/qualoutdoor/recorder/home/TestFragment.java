package com.qualoutdoor.recorder.home;

import java.util.List;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.qualoutdoor.recorder.R;
import com.qualoutdoor.recorder.ServiceListener;
import com.qualoutdoor.recorder.ServiceProvider;
import com.qualoutdoor.recorder.telephony.ICellInfo;
import com.qualoutdoor.recorder.telephony.TelephonyContext;
import com.qualoutdoor.recorder.telephony.TelephonyListener;
import com.qualoutdoor.recorder.telephony.TelephonyService;
import com.qualoutdoor.recorder.telephony.ViewCellInfo;

/**
 * This fragment displays the main informations of the phone state on a single
 * screen. Its parent activity must implements the interface TelephonyContext.
 */
public class TestFragment extends Fragment {

    /** The events monitored by the Telephony Listener */
    private static final int events = TelephonyListener.LISTEN_CELL_INFO;

    /**
     * The Telephony Listener, which defines the behavior against telephony
     * state changesnewCellInfo
     */
    private TelephonyListener telListener = new TelephonyListener() {

        @Override
        public void onCellInfoChanged(List<ICellInfo> cellInfos) {
            Log.d("NetworkFragment", "OnCellInfoChanged");
                // Find the first registered cell
                for (ICellInfo cell : cellInfos) {
                    if (cell.isRegistered()) {
                        cellInfo = cell;
                        // Update the Cell Info view
                        updateCellInfo();
                    }
            }
        }

    };

    /** The TelephonyService Provider given by the activity */
    private ServiceProvider<TelephonyService> telephonyService;
    /**
     * The service listener defines the behavior when the service becomes
     * available
     */
    private ServiceListener<TelephonyService> telServiceListener = new ServiceListener<TelephonyService>() {
        @Override
        public void onServiceAvailable(TelephonyService service) {
            // Register the telephony listener
            telephonyService.getService().listen(telListener, events);
        }
    };

    /** The network type code strings */
    private String[] networkNames;

    /** The first ViewCellInfo */
    ViewCellInfo viewCellInfo;
    ICellInfo cellInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Initialize the network names from the ressources
        networkNames = getResources().getStringArray(R.array.network_type_name);
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
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d("NetworkFragment", "onConfigurationChanged");

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        ScrollView view = (ScrollView) inflater.inflate(R.layout.fragment_test,
                container, false);

        viewCellInfo = (ViewCellInfo) view.findViewById(R.id.cell_info);
        if(cellInfo != null) {
            updateCellInfo();
        }
        
        LinearLayout layout = ((LinearLayout) view
                .findViewById(R.id.fragment_test_linear_layout));

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
        if (telephonyService.isAvailable()) {
            telephonyService.getService().listen(telListener,
                    TelephonyListener.LISTEN_NONE);
        }
        // Unregister the services listeners
        telephonyService.unregister(telServiceListener);
        super.onPause();
    }

    /** Update the Cell Info view */
    private void updateCellInfo() {
        
    if(viewCellInfo != null)
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewCellInfo.updateCellInfo(cellInfo);
            }
        });
    }

}