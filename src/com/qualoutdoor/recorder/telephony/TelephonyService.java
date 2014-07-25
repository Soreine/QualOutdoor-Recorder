package com.qualoutdoor.recorder.telephony;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * This service is an Android implementation of ITelephony, it uses a
 * TelephonyManager to access phone state informations. An app component can
 * bind to it anytime in order to monitor the phone state.
 */
public class TelephonyService extends Service implements ITelephony {

	/** The interface binder for this service */
	private IBinder mTelephonyBinder;

	/** An instance of TelephonyManager */
	private TelephonyManager telephonyManager;

	/** The events the phone state listener is monitoring */
	@SuppressLint("InlinedApi")
	private static int events = PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
			| PhoneStateListener.LISTEN_CALL_STATE
			| PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
			| PhoneStateListener.LISTEN_SERVICE_STATE
			| PhoneStateListener.LISTEN_CELL_INFO; // TODO support for earlier
													// version (see
													// NeighboringCellInfo)

	/** The Android phone state listener */
	private PhoneStateListener phoneStateListener = new PhoneStateListener() {
		@Override
		public void onSignalStrengthsChanged(SignalStrength ss) {
			// Update our signal strength instance
			TelephonyService.this.signalStrength.setSignalStrength(ss);
		}

		@Override
		public void onDataConnectionStateChanged(int state, int networkType) {
			// Update the current data connection state
			TelephonyService.this.dataState = state;
			// Update the current network type
			TelephonyService.this.networkType = networkType;
		};

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// Update the current call state
			TelephonyService.this.callState = state;
		};

		@Override
		public void onCellInfoChanged(List<android.telephony.CellInfo> cellInfo) {
		};
	};

	/** The current signal strength value */
	private CustomSignalStrength signalStrength;
	/** The current data connection state */
	private int dataState;
	/** The current network type */
	private int networkType;
	/** The current call state */
	private int callState;

	/** TODO The current cell infos */

	@Override
	public void onCreate() {
		// Initialize a TelephonyBinder that knows this Service
		mTelephonyBinder = new TelephonyBinder(this);

		// Retrieve an instance of Telephony Manager
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		// Initialize the current phone state values
		{
			// Initialize the signal strength
			signalStrength = new CustomSignalStrength(null);
			// Initialize the call state
			callState = telephonyManager.getCallState();
			// Initialize the network type
			networkType = telephonyManager.getNetworkType();
			// Initialize the data state
			dataState = telephonyManager.getDataState();
		}
		
		// Start listening to phone state
		telephonyManager.listen(phoneStateListener, events);

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// Unregister our listener from the telephony manager system service
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("TelephonyService", intent.toString());
		// Return our interface binder
		return mTelephonyBinder;
	}

	@Override
	public List<ICellInfo> getAllCellInfo() {
		// TODO convert the List<CellInfo> from TelephonyManager to a
		// List<ICellInfo>
		return new ArrayList<ICellInfo>();
	}

	@Override
	public int getCallState() {
		// We normally should convert the call state code given by
		// TelephonyManager to the given code in the ITelephony interface. But
		// as today they are the same.
		return callState;
	}

	@Override
	public int getDataState() {
		// We normally should convert the data state code given by
		// TelephonyManager to the given code in the ITelephony interface. But
		// as today they are the same.
		return dataState;
	}

	@Override
	public int getNetworkType() {
		// We normally should convert the network type code given by
		// TelephonyManager to the given code in the ITelephony interface. But
		// as today they are the same.
		return networkType;
	}

	@Override
	public ILocation getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISignalStrength getSignalStrength() {
		// Return the current signal strength
		return signalStrength;
	}

	@Override
	public void listen(TelephonyListener listener, int events) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setMinimumRefreshRate(int milliseconds) {
		// TODO Auto-generated method stub

	}

}
