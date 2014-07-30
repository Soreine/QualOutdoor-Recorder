package com.qualoutdoor.recorder;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qualoutdoor.recorder.telephony.TelephonyContext;
import com.qualoutdoor.recorder.telephony.TelephonyService;

/**
 * This fragment displays the main informations of the phone state on a single
 * screen. Its parent activity must implements the interface
 * TelephonyServiceConnectionProvider.
 */
public class OverviewFragment extends Fragment {

	/** The TelephonyServiceConnection used to connect to the TelephonyService */
	private ServiceProvider<TelephonyService> telephonyService;

	/** The signal strength value text view */
	private TextView viewSignalStrength;
	/** The gps location text view */
	private TextView viewGPSLocation;
	/** The mobile network code text view */
	private TextView viewMobileNetworkCode;
	/** The network type code text view */
	private TextView viewNetworkTypeCode;
	/** The mobile country code text view */
	private TextView viewMobileCountryCode;

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
					+ " must implement "
					+ TelephonyContext.class.toString());
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
		viewNetworkTypeCode = (TextView) view
				.findViewById(R.id.network_type_code_value);
		viewMobileCountryCode = (TextView) view
				.findViewById(R.id.mobile_country_code_value);
		return view;
	}

	@Override
	public void onStart() {
		if(telephonyService.isAvailable()) {
			TelephonyService service = telephonyService.getService();
			// Fill in the view fields values
			viewSignalStrength.setText(service.getSignalStrength().getDbm() + " (dBm)");
		
			// Retrieve the network type code strings
			String[] networkNames = getResources().getStringArray(R.array.network_type_name);
			viewNetworkTypeCode.setText(networkNames[service.getNetworkType()]);
		}
		else {
			Log.d(this.getClass().toString(), "not available :(");
		}
		super.onStart();
	}
}