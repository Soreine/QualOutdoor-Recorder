package com.qualoutdoor.recorder.map;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.qualoutdoor.recorder.R;
import com.qualoutdoor.recorder.IServiceListener;
import com.qualoutdoor.recorder.ServiceProvider;
import com.qualoutdoor.recorder.ServiceProvider.ServiceNotBoundException;
import com.qualoutdoor.recorder.location.LocationContext;
import com.qualoutdoor.recorder.location.LocationService;
import com.qualoutdoor.recorder.telephony.ISignalStrength;
import com.qualoutdoor.recorder.telephony.TelephonyContext;
import com.qualoutdoor.recorder.telephony.TelephonyListener;
import com.qualoutdoor.recorder.telephony.TelephonyService;

/**
 * An demo map fragment that displays signal strengths on a map
 * 
 * @author Gaborit Nicolas
 */
public class DataMapFragment extends Fragment implements LocationListener {

    private static final float startHue = 0;
    private static final float endHue = 120;

    private ISignalStrength signalStrength;

    // Reference to the Map object
    private GoogleMap map;
    // Reference to the MapFragment
    private SupportMapFragment mapFragment;

    private ServiceProvider<TelephonyService> telephonyService;

    private ServiceProvider<LocationService> locationService;

    private IServiceListener<TelephonyService> telServiceListener = new IServiceListener<TelephonyService>() {
        @Override
        public void onServiceAvailable(TelephonyService service) {
            // Register the telephony listener
            service.listen(telListener, events);
        };
    };

    private static final int events = TelephonyListener.LISTEN_SIGNAL_STRENGTHS;
    private TelephonyListener telListener = new TelephonyListener() {
        @Override
        public void onSignalStrengthsChanged(ISignalStrength signalStrength) {
            // Update signal strength
            DataMapFragment.this.signalStrength = signalStrength;
        };
    };

    private IServiceListener<LocationService> locServiceListener = new IServiceListener<LocationService>() {
        @Override
        public void onServiceAvailable(LocationService service) {
            // Register as a listener
            LocationRequest locationRequest = new LocationRequest()
                    .setInterval(2000);
            service.requestLocationUpdates(locationRequest,
                    DataMapFragment.this);
        }
    };
    private Location location;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // This cast makes sure that the container activity has implemented
            // TelephonyContext
            TelephonyContext telephonyContext = (TelephonyContext) activity;

            // Retrieve the service connection
            telephonyService = telephonyContext.getTelephonyServiceProvider();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + TelephonyContext.class.toString());
        }
        try {
            // This cast makes sure that the container activity has implemented
            // LocationContext
            LocationContext locationContext = (LocationContext) activity;

            // Retrieve the service connection
            locationService = locationContext.getLocationServiceProvider();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + LocationContext.class.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if we're being restored from a previous state (in which
        // case the Fragment is already initialized)
        if (savedInstanceState != null) {
            // Do nothing, otherwise we could end up with overlapping
            // fragments
            return;
        }

        // Create options for the Google Map
        GoogleMapOptions options = new GoogleMapOptions();
        options.tiltGesturesEnabled(false)
                .mapType(GoogleMap.MAP_TYPE_SATELLITE).compassEnabled(false);// .camera(new
                                                                             // CameraPosition.Builder().zoom(15).build());

        // Create a new MapFragment to be placed in the fragment layout
        mapFragment = SupportMapFragment.newInstance(options);

        // TODO : see what does mapFragment.setRetainInstance(true);
        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction()
                .add(R.id.map_container, mapFragment).commit();

    }

    // Called when the fragment has to instantiate its own view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the view from the xml layout file
        View rootView = inflater.inflate(R.layout.fragment_map, container,
                false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check that the map is available
        if (setUpMapIfNeeded()) {
            Log.d("Map", "Map available");
        } else {
            Log.d("Map", "NO MAP AVAILABLE");
        }

        // Tell we want to be informed when services become available
        telephonyService.register(telServiceListener);
        locationService.register(locServiceListener);
    }

    @Override
    public void onPause() {
        // If needed unregister our telephony listener
        try {
            telephonyService.getService().listen(telListener,
                    TelephonyListener.LISTEN_NONE);
        } catch (ServiceNotBoundException e) {}
        // Unregister location listener
        try {
            locationService.getService().removeLocationUpdate(this);
        } catch (ServiceNotBoundException e) {}

        // Unregister the services listeners
        telephonyService.unregister(telServiceListener);
        locationService.unregister(locServiceListener);
        super.onPause();
    }

    /**
     * Instantiate the Map object from the MapFragment if needed. This is called
     * from onCreate and onResume to ensure that the map is always available.
     * Returns true if the map is available.
     */
    private boolean setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (map == null) {
            // Obtain the Map object from the MapFragment
            if (mapFragment == null) {
                mapFragment = (SupportMapFragment) getFragmentManager()
                        .findFragmentById(R.id.map_container);
            }
            map = mapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (map == null) {
                // Unsuccesful
                return false;
            }
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Update location
        this.location = location;
        if (map != null) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LatLng latlng = new LatLng(DataMapFragment.this.location
                            .getLatitude(), DataMapFragment.this.location
                            .getLongitude());
                    // Center the camera on the new location
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                            latlng, 19);

                    // Move the camera
                    map.animateCamera(update);

                    // Add a marker
                    createMarker(latlng);

                }
            });
        }
    }

    private void createMarker(LatLng latlng) {
        float[] hsv = new float[3];
        hsv[1] = 255f;
        hsv[2] = 255f;
        float relativeAsu = ((float) signalStrength.getAsuLevel()) / 31; // MAX_ASU
        hsv[0] = startHue + relativeAsu * (endHue - startHue);
        // Instantiates a new CircleOptions object and defines the radius in
        // meters
        CircleOptions circleOptions = new CircleOptions().radius(1)
                .strokeColor(0x00000000).center(latlng)
                .fillColor(Color.HSVToColor(hsv));
        map.addCircle(circleOptions);
    }

}
