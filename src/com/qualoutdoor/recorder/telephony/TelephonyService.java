package com.qualoutdoor.recorder.telephony;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.CellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.qualoutdoor.recorder.LocalBinder;

/**
 * This service is an Android implementation of ITelephony, it uses a
 * TelephonyManager to access phone state informations. An app component can
 * bind to it anytime in order to monitor the phone state.
 */
public class TelephonyService extends Service implements ITelephony,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    /** The default value asked for location update (millis) */
    private static final int DEFAULT_LOCATION_UPDATE_INTERVAL = 2000;
    /** The fastest location update interval we can handle (millis) */
    private static final int FASTEST_LOCATION_INTERVAL = 1000;

    /** This is the estimated max size for the cell info array list */
    private static final int ESTIMATED_MAX_CELLS = 10;

    /** The interface binder for this service */
    private IBinder mTelephonyBinder;

    /** The current signal strength value */
    private ISignalStrength signalStrength;
    /** The current data connection state */
    private int dataState;
    /** The current network type */
    private int networkType;
    /** The current call state */
    private int callState;
    /** The incomingNumber */
    private String incomingNumber = "";
    /** The current location */
    private ILocation location;
    /** The current visible cells */
    private ArrayList<ICellInfo> allCellInfos;

    /****** The listeners list ******/
    // Note : Might use CopyOnWriteArrayList to avoid
    // ConcurrentModificationExceptions if a
    // listener attempts to remove itself during event notification.
    /** Store the listeners listening to LISTEN_CALL_STATE */
    private ArrayList<TelephonyListener> listenersCallState = new ArrayList<TelephonyListener>();
    /** Store the listeners listening to LISTEN_CELL_INFO */
    private ArrayList<TelephonyListener> listenersCellInfo = new ArrayList<TelephonyListener>();
    /** Store the listeners listening to LISTEN_DATA_STATE */
    private ArrayList<TelephonyListener> listenersDataState = new ArrayList<TelephonyListener>();
    /** Store the listeners listening to LISTEN_LOCATION */
    private ArrayList<TelephonyListener> listenersLocation = new ArrayList<TelephonyListener>();
    /** Store the listeners listening to LISTEN_SIGNAL_STRENGTHS */
    private ArrayList<TelephonyListener> listenersSignalStrength = new ArrayList<TelephonyListener>();

    /** An instance of TelephonyManager */
    private TelephonyManager telephonyManager;

    /** The events the phone state listener is monitoring */
    private static int events = PhoneStateListener.LISTEN_CALL_STATE
            | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
            | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
            | PhoneStateListener.LISTEN_SERVICE_STATE
            | PhoneStateListener.LISTEN_CELL_INFO;

    /** The Android phone state listener */
    private PhoneStateListener phoneStateListener = new PhoneStateListener() {

        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            // Update the current data connection state
            TelephonyService.this.dataState = state;
            // Update the current network type
            TelephonyService.this.networkType = networkType;
            // Notify the data state listeners
            notifyDataStateListeners(state, networkType);
        };

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // Update the current call state
            TelephonyService.this.callState = state;
            // Update the incomingNumber
            TelephonyService.this.incomingNumber = incomingNumber;
            // Notify ththat knowse call state listeners
            notifyCallStateListeners(state, incomingNumber);
        };

        @Override
        public void onCellInfoChanged(List<CellInfo> newCellInfos) {
            Log.d("TelephonyService", "onCellInfoChanged");

            // TODO Error log
            List<CellInfo> cellInfos = newCellInfos;
            // Return if cellInfos is null
            if (cellInfos == null) {
                Log.d("TelephonyService", "newCellInfo = null");
                // Try getAllCellInfos
                cellInfos = telephonyManager.getAllCellInfo();
                if (cellInfos == null) {
                    Log.d("TelephonyService", "getAllCellInfo = null");
                    return;
                }
            }
            // Update the cell infos and notify
            updateCellInfos(cellInfos);
        };

        @Override
        public void onSignalStrengthsChanged(
                android.telephony.SignalStrength signalStrength) {
            // TODO We are not currently able to parse a SignalStrength so we
            // just update the CellInfo list instead. This has the effect to
            // update the signal strength value ;)
            updateCellInfos(telephonyManager.getAllCellInfo());
        };
    };

    /** Our location request reference */
    private final LocationRequest locationRequest;
    {
        // Create a location request object
        locationRequest = LocationRequest.create();
        // Set accuracy to high
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval equal to default
        locationRequest.setInterval(DEFAULT_LOCATION_UPDATE_INTERVAL);
        // Set the fastest update interval
        locationRequest.setFastestInterval(FASTEST_LOCATION_INTERVAL);
    }

    /** Flag that indicates if a location request is underway */
    private boolean locationInProgress;

    /** Our location client reference */
    private LocationClient locationClient;

    @Override
    public void onCreate() {
        // Initialize telephony objects
        {
            // Initialize a TelephonyBinder linked to this Service
            mTelephonyBinder = new LocalBinder<TelephonyService>(this);

            // Retrieve an instance of Telephony Manager
            telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            // Initialize the current phone state values
            {
                // Initialize the signal strength
                signalStrength = new CustomSignalStrength();
                // Initialize the call state
                callState = telephonyManager.getCallState();
                // Initialize the network type
                networkType = telephonyManager.getNetworkType();
                // Initialize the data state
                dataState = telephonyManager.getDataState();
                // Initialize the cell list
                allCellInfos = new ArrayList<ICellInfo>(ESTIMATED_MAX_CELLS);
            }

            // Start listening to phone state
            telephonyManager.listen(phoneStateListener, events);
        }

        // Initialize locations objects
        {
            // No location request is underway
            locationInProgress = false;
            // Create a new location client using this class to handle callbacks
            locationClient = new LocationClient(this, this, this);
            
            
        }
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
        // Return our interface binder
        return mTelephonyBinder;
    }

    @Override
    public List<ICellInfo> getAllCellInfo() {
        return allCellInfos;
    }

    @Override
    public int getCallState() {
        // We normally should convert the call state code given by
        // TelephonyManager to the given code in the ITelephony interface. But
        // they are the same so far.
        return callState;
    }

    @Override
    public int getDataState() {
        // We normally should convert the data state code given by
        // TelephonyManager to the given code in the ITelephony interface. But
        // they are the same so far.
        return dataState;
    }

    @Override
    public int getNetworkType() {
        // We normally should convert the network type code given by
        // TelephonyManager to the given code in the ITelephony interface. But
        // they are the same so far.
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
        // Check if we are unregistering a listener
        if (events == TelephonyListener.LISTEN_NONE) {
            // Unregister the listener from all the lists
            listenersCallState.remove(listener);
            listenersCellInfo.remove(listener);
            listenersDataState.remove(listener);
            listenersLocation.remove(listener);
            listenersSignalStrength.remove(listener);
        } else {
            Log.d("TelephonyService", "Registering " + events);
            // Add the listener to the corresponding list, according to the
            // events
            // it is subscribing to.
            // We are making bitwise comparison because 'events' is used as a
            // boolean mask.
            if ((events & TelephonyListener.LISTEN_CALL_STATE) == TelephonyListener.LISTEN_CALL_STATE) {
                // The listener wish to monitor the call state, add it to the
                // list
                listenersCallState.add(listener);
                // Notify it immediatly with the current data
                listener.onCallStateChanged(callState, incomingNumber);
            }
            if ((events & TelephonyListener.LISTEN_CELL_INFO) == TelephonyListener.LISTEN_CELL_INFO) {
                // The listener wish to monitor the cells infos, add it to the
                // list
                listenersCellInfo.add(listener);
                // Notify it immediatly with a read only copy of allCellInfos
                listener.onCellInfoChanged(Collections
                        .unmodifiableList(allCellInfos));
            }
            if ((events & TelephonyListener.LISTEN_DATA_STATE) == TelephonyListener.LISTEN_DATA_STATE) {
                // The listener wish to monitor the data state, add it to the
                // list
                listenersDataState.add(listener);
                // Notify it immediatly with the current data
                listener.onDataStateChanged(dataState, networkType);
            }
            if ((events & TelephonyListener.LISTEN_LOCATION) == TelephonyListener.LISTEN_LOCATION) {
                // The listener wish to monitor the location, add it to the list
                listenersLocation.add(listener);
                // Notify it immediatly with the current data
                listener.onLocationChanged(location);
            }
            if ((events & TelephonyListener.LISTEN_SIGNAL_STRENGTHS) == TelephonyListener.LISTEN_SIGNAL_STRENGTHS) {
                // The listener wish to monitor the signal strength, add it to
                // the list
                listenersSignalStrength.add(listener);
                // Notify it immediatly with the current data
                listener.onSignalStrengthsChanged(signalStrength);
            }
        }
    }

    @Override
    public void setMinimumRefreshRate(int milliseconds) {
        // TODO Auto-generated method stub

    }

    /**
     * Parse a CellInfo list and update the allCellInfo, then notify the
     * listeners from the changes
     */
    private void updateCellInfos(List<CellInfo> cellInfos) {
        // Reset the cell info array list
        allCellInfos.clear();

        // Declare the variable holding the converted cell
        CustomCellInfo customCell;
        // Update the current ICellInfo list and retrieve the signal
        // strength at the same time.
        for (CellInfo cell : cellInfos) {
            // Create a corresponding ICellInfo and
            customCell = CustomCellInfo.buildFromCellInfo(cell);
            // Add it to the list
            allCellInfos.add(customCell);
            // Check if this is the primary cell
            if (customCell.isRegistered()) {
                Log.d("TelephonyService", "Registered " + cell.toString());
                // This is the signal strength you are looking for
                signalStrength = customCell.getSignalStrength();
                // Notify the signal strength listeners
                notifySignalStrengthListeners(signalStrength);
            }
        }
        // Create a non modifiable ICellInfo list
        List<ICellInfo> unmodifiableCellInfo = Collections
                .unmodifiableList(allCellInfos);
        // Notify the cell info listeners
        notifyCellInfoListeners(unmodifiableCellInfo);
    }

    /** Notify each cell info listeners with the current ICellInfo list */
    private void notifyCellInfoListeners(List<ICellInfo> cellInfos) {
        for (TelephonyListener listener : listenersCellInfo) {
            // For each listener, notify
            listener.onCellInfoChanged(cellInfos);
            Log.d("TelephonyService", "notify " + listener.toString());
        }
    }

    /**
     * Notify each signal strength listeners with the current ISignalStrength
     * value
     */
    private void notifySignalStrengthListeners(ISignalStrength signalStrength) {
        Log.d("TelephonyService", "notifySignalStrengthListeners");
        for (TelephonyListener listener : listenersCellInfo) {
            Log.d("TelephonyService", "listener.onSignalStrengthChanged "
                    + listener.toString());
            // For each listener, notifyallCellInfosUnmodifiable
            listener.onSignalStrengthsChanged(signalStrength);
        }
    }

    /**
     * Notify each call state listeners with the current call state and network
     * type values
     */
    private void notifyCallStateListeners(int state, String incomingNumber) {
        for (TelephonyListener listener : listenersCellInfo) {
            // For each listener, notify
            listener.onCallStateChanged(state, incomingNumber);
        }
    }

    /** Notify each location listeners with the current ILocation value */
    private void notifyDataStateListeners(int state, int networkType) {
        for (TelephonyListener listener : listenersCellInfo) {
            // For each listener, notify
            listener.onDataStateChanged(state, networkType);
        }
    }

    /** Notify each location listeners with the current ILocation value */
    private void notifyLocationListeners(ILocation location) {
        for (TelephonyListener listener : listenersCellInfo) {
            // For each listener, notify
            listener.onLocationChanged(location);
        }
    }

    /************ Location related method ****************/

    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub
    }

    /** Check whether the Google Play Services are available */
    private boolean areServicesConnected() {
        // Check that Google Play services are available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // Return if Google Play services are available
        return (ConnectionResult.SUCCESS == resultCode);
    }
}
