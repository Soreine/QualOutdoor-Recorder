package com.qualoutdoor.recorder.telephony;

import java.util.ArrayList;
import java.util.List;

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
	/** The Android phone state listener */
	private PhoneStateListener phoneStateListener = new PhoneStateListener() {		
		@Override
		public void onSignalStrengthsChanged(SignalStrength ss) {
			signalStrength.setSignalStrength(ss);
			super.onSignalStrengthsChanged(ss);
		}
	};
	/** The events the phone state listener is monitoring */
	private static int events = PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;
	
	/** The current signal strength value */
	private CustomSignalStrength signalStrength;
	
	@Override
	public void onCreate() {
		// Initialize a TelephonyBinder that knows this Service
		mTelephonyBinder = new TelephonyBinder(this);

		// Initialize the current signal strength object
		signalStrength = new CustomSignalStrength(null);
		
		// Retrieve an instance of Telephony Manager
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// Start listening to phone state
		telephonyManager.listen(phoneStateListener, events);
		
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// Unregister our listener from the telephony manager system service
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("TelephonyService",intent.toString());
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
		return telephonyManager.getCallState();
	}

	@Override
	public int getDataState() {
		// We normally should convert the data state code given by
		// TelephonyManager to the given code in the ITelephony interface. But
		// as today they are the same.
		return telephonyManager.getDataState();
	}

	@Override
	public int getNetworkType() {
		// We normally should convert the network type code given by
		// TelephonyManager to the given code in the ITelephony interface. But
		// as today they are the same.
		return telephonyManager.getNetworkType();
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
