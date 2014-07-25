package com.qualoutdoor.recorder.telephony;

/**
 * Activities using fragments that require access to
 * TelephonyService must implement this interface.
 */
public interface TelephonyServiceConnectionProvider  {
	
	TelephonyServiceConnection getTelephonyServiceConnection();

}
