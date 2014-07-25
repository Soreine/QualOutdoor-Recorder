package com.qualoutdoor.recorder.telephony;

import android.telephony.SignalStrength;

public class CustomSignalStrength implements ISignalStrength {

	/** We encapsulate an Android SignalStrength instance */
	private SignalStrength ss;

	/**
	 * We can create a CustomSignalStrength from the Android SignalStrength
	 * class
	 */
	public CustomSignalStrength(SignalStrength ss) {
		this.ss = ss;
	}

	/** Modify the signal strength value */
	public void setSignalStrength(SignalStrength ss) {
		this.ss = ss;
	}

	@Override
	public int getDbm() {
		// TODO check which type of signal strength we have
		return 2*ss.getGsmSignalStrength() - 113;
	}

}
