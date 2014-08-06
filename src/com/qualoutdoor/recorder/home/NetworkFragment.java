package com.qualoutdoor.recorder.home;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

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
public class NetworkFragment extends Fragment {

    /** The events monitored by the Telephony Listener */
    private static final int events = TelephonyListener.LISTEN_CELL_INFO
            | TelephonyListener.LISTEN_DATA_STATE;
    /**
     * The Telephony Listener, which defines the behavior against telephony
     * state changes
     */
    private TelephonyListener telListener = new TelephonyListener() {
        @Override
        public void onCellInfoChanged(List<ICellInfo> cellInfos) {
            // Find the number of neighbors cells
            neighborsCount = cellInfos.size();
            // Find the first registered cell
            for (ICellInfo cell : cellInfos) {
                if (cell.isRegistered()) {
                    // This is the primary cell
                    cellInfo = cell;
                    // Don't count it in the neighbors count
                    neighborsCount--;

                    // Stop searching
                    break;
                }
            }
            // Update the UI elements
            updateCellInfo();
        }

        public void onDataStateChanged(int state, int networkType) {
            // Update the network type
            network = networkType;
            // Update the UI
            updateNetworkTypeView();
        };
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

    /** The network type code */
    private int network;
    /** The primary cell */
    private ICellInfo cellInfo;
    /** The number of detected neighboring cells */
    private int neighborsCount;

    /** The network type code text view */
    private TextView viewNetworkType;
    /** The primary cell view */
    private ViewCellInfo viewCellInfo;
    /** The view indicating the number of neighboring cells */
    private TextView viewNeighborsCount;
    /** The network type code strings */
    private String[] networkNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Initialize the network names from the ressources
        networkNames = getResources().getStringArray(R.array.network_type_name);
        // No neighbors have been detected yet
        neighborsCount = 0;
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
        ScrollView view = (ScrollView) inflater.inflate(
                R.layout.fragment_network, container, false);
        // Initialize the views references
        viewNetworkType = (TextView) view
                .findViewById(R.id.network_type_code_value);
        viewNeighborsCount = (TextView) view
                .findViewById(R.id.neighbors_count_value);

        viewCellInfo = (ViewCellInfo) view
                .findViewById(R.id.fragment_network_cell_info);

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

    /** Update the text field with the current value of network type */
    private void updateNetworkTypeView() {
        // Check that the view has been initialized
        if (viewNetworkType != null) {
            // Access the UI from the main thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewNetworkType.setText(networkNames[network]);
                    // Invalidate the view that changed
                    viewNetworkType.invalidate();
                }
            });
        }
    }

    /** Update the Cell Info view */
    private void updateCellInfo() {
        // Check that the view has been initialized
        if (viewCellInfo != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Update the ViewCellInfo
                    viewCellInfo.updateCellInfo(cellInfo);
                    // Update the number of neighbors
                    viewNeighborsCount.setText("" + neighborsCount);
                }
            });
    }

}