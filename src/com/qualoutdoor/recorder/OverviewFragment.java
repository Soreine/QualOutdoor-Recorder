package com.qualoutdoor.recorder;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.qualoutdoor.recorder.location.LocationContext;
import com.qualoutdoor.recorder.location.LocationService;
import com.qualoutdoor.recorder.telephony.ICellInfo;
import com.qualoutdoor.recorder.telephony.ISignalStrength;
import com.qualoutdoor.recorder.telephony.TelephonyContext;
import com.qualoutdoor.recorder.telephony.TelephonyListener;
import com.qualoutdoor.recorder.telephony.TelephonyService;

/**
 * This fragment displays the main informations of the phone state on a single
 * screen. Its parent activity must implements the interface TelephonyContext.
 */
public class OverviewFragment extends Fragment implements LocationListener {

    /** The events monitored by the Telephony Listener */
    private static final int events = TelephonyListener.LISTEN_CELL_INFO
            | TelephonyListener.LISTEN_SIGNAL_STRENGTHS
            | TelephonyListener.LISTEN_DATA_STATE;
    /**
     * The Telephony Listener, which defines the behavior against telephony
     * state changes
     */
    private TelephonyListener telListener = new TelephonyListener() {
        public void onSignalStrengthsChanged(ISignalStrength signalStrength) {
            // Update the signal strength
            OverviewFragment.this.signalStrength = signalStrength;
            // Update the UI
            updateSignalStrengthView();
        };

        @Override
        public void onCellInfoChanged(List<ICellInfo> cellInfos) {
            Log.d("OverviewFragment", "OnCellInfoChanged");
            // Find the first registered cell
            for (ICellInfo cell : cellInfos) {
                if (cell.isRegistered()) {
                    // We assume this is the primary cell
                    // Update the MCC
                    mcc = cell.getMcc();
                    // Update the MNC
                    mnc = cell.getMnc();
                    // Update the UI
                    updateMCCView();
                    updateMNCView();
                    // Stop searching
                    break;
                }
            }
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

    /** A reference to the LocationService Provider given by the activity */
    private ServiceProvider<LocationService> locationService;

    /**
     * The service listener defines the behavior when the service becomes
     * available
     */
    private ServiceListener<LocationService> locServiceListener = new ServiceListener<LocationService>() {
        @Override
        public void onServiceAvailable(LocationService service) {
            Log.d("OverviewFragment", "onServiceAvailable LocationService");
            // Register the fragment as a location listener
            locationService.getService().register(OverviewFragment.this);
        }
    };

    /** The signal strength value */
    private ISignalStrength signalStrength;
    /** The gps location */
    private Location location;
    /** The mobile network code */
    private int mnc;
    /** The network type code */
    private int network;
    /** The mobile country code */
    private int mcc;

    /** The signal strength value text view */
    private TextView viewSignalStrength;
    /** The gps location text view */
    private TextView viewGPSLocation;
    /** The mobile network code text view */
    private TextView viewMobileNetworkCode;
    /** The network type code text view */
    private TextView viewNetworkType;
    /** The mobile country code text view */
    private TextView viewMobileCountryCode;

    /** The network type code strings */
    private String[] networkNames;

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
        try {
            // This cast makes sure that the container activity has implemented
            // LocationContext
            LocationContext locationContext = (LocationContext) getActivity();

            // Retrieve the service connection
            locationService = locationContext.getLocationServiceProvider();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + LocationContext.class.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container,
                false);
        // Initialize the views references
        viewSignalStrength = (TextView) view
                .findViewById(R.id.signal_strength_value);
        viewGPSLocation = (TextView) view.findViewById(R.id.gps_location_value);
        viewMobileNetworkCode = (TextView) view
                .findViewById(R.id.mobile_network_code_value);
        viewNetworkType = (TextView) view
                .findViewById(R.id.network_type_code_value);
        viewMobileCountryCode = (TextView) view
                .findViewById(R.id.mobile_country_code_value);
        return view;
    }

    @Override
    public void onResume() {
        // Tell we want to be informed when services become available
        telephonyService.register(telServiceListener);
        locationService.register(locServiceListener);
        super.onStart();
    }

    @Override
    public void onPause() {
        // If needed unregister our telephony listener
        if (telephonyService.isAvailable()) {
            telephonyService.getService().listen(telListener,
                    TelephonyListener.LISTEN_NONE);
        }
        // Unregister location listener
        if (locationService.isAvailable()) {
            locationService.getService().unregister(this);
        }
        // Unregister the services listeners
        telephonyService.unregister(telServiceListener);
        locationService.unregister(locServiceListener);
        super.onPause();
    }

    /** Update the text field with the current value of signal strength */
    private void updateSignalStrengthView() {
        // Check that the view has been initialized
        if (viewSignalStrength != null) {
            // Access the UI from the main thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Fill in the view fields values
                    viewSignalStrength.setText(signalStrength.getAsuLevel()
                            + " (asu)");
                    // Invalidate the view that changed
                    viewSignalStrength.invalidate();
                }
            });
        }
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

    /** Update the text field with the current value of MNC */
    private void updateMNCView() {
        // Check that the view has been initialized
        if (viewMobileNetworkCode != null) {
            // Access the UI from the main thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewMobileNetworkCode.setText(mnc + "");
                    // Invalidate the view that changed
                    viewMobileNetworkCode.invalidate();
                }
            });
        }
    }

    /** Update the text field with the current value of MCC */
    private void updateMCCView() {
        // Check that the view has been initialized
        if (viewMobileCountryCode != null) {
            // Access the UI from the main thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewMobileCountryCode.setText(mcc + "");
                    // Invalidate the view that changed
                    viewMobileCountryCode.invalidate();
                }
            });
        }
    }

    /** Update the text field with the current GPS location */
    private void updateGPS() {
        // Check that the view has been initialized
        if (viewGPSLocation != null) {
            // Access the UI from the main thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DecimalFormat format = new DecimalFormat("0.##");
                    viewGPSLocation.setText(format.format(location
                            .getLatitude())
                            + "° "
                            + format.format(location.getLongitude()) + "°");
                    // Invalidate the view that changed
                    viewGPSLocation.invalidate();
                }
            });
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Update the location
        this.location = location;
        // Update the UI
        updateGPS();
    }

}