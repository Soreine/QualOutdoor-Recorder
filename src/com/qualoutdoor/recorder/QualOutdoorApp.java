package com.qualoutdoor.recorder;

import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Application;
import android.telephony.TelephonyManager;

/**
 * The application class, it holds attributes and methods that are global to the
 * application
 */
public class QualOutdoorApp extends Application {

	/** An interface for network changes listeners */
	public interface NetworkChangeListener {
		public void onNetworkChanged(int newNetwork);
	}

	/** The registered network changes listeners */
	// Use CopyOnWriteArrayList to avoid ConcurrentModificationExceptions if a
	// listener attempts to remove itself during event notification.
	private final CopyOnWriteArrayList<NetworkChangeListener> networkListeners;

	/// The current network type value
	private int currentNetwork = TelephonyManager.NETWORK_TYPE_UNKNOWN;
	
	
	/** The application constructor */
	public QualOutdoorApp() {
		this.networkListeners = new CopyOnWriteArrayList<QualOutdoorApp.NetworkChangeListener>();
	}

	/** Get the current network value */
	public int getCurrentNetwork() {
		return currentNetwork;
	}
	/** Modify the current network value */
	public void setCurrentNetwork(int network) {
		this.currentNetwork = network;
		notifyNetworkListeners(network);
	}

	/** Add a new listener */
	public void addNetworkChangeListener(NetworkChangeListener l) {
		this.networkListeners.add(l);
	}

	/** Remove a listener to the list */
	public void removeNetworkChangeListener(NetworkChangeListener l) {
		this.networkListeners.remove(l);
	}

	/** Notifies the network changes listeners that the network type has changed */
	public void notifyNetworkListeners(int newNetwork) {
		for (NetworkChangeListener l : networkListeners) {
			l.onNetworkChanged(newNetwork);
		}
	}
}
