package com.qualoutdoor.recorder.map;

import java.util.Random;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.qualoutdoor.recorder.R;
import com.qualoutdoor.recorder.R.id;
import com.qualoutdoor.recorder.R.layout;

public class DataMapFragment extends Fragment {

	private static int numMarkers = 25;
	private static double melbLat = -37.813;
	private static double melbLng = 144.962;
	private static LatLng melbournecenter = new LatLng(melbLat, melbLng);
	private static LatLng southwest = new LatLng(melbLat - 0.015,
			melbLng - 0.015);
	private static LatLng northwest = new LatLng(melbLat + 0.015,
			melbLng + 0.015);
	private static LatLngBounds melbourne = new LatLngBounds(southwest,
			northwest);
	private static double lat = northwest.latitude - southwest.latitude;
	private static double lng = northwest.longitude - southwest.longitude;

	float startHue = 233;
	float endHue = 170;

	// Reference to the Map object
	private GoogleMap map;
	// Reference to the MapFragment
	private SupportMapFragment mapFragment;

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
				.mapType(GoogleMap.MAP_TYPE_TERRAIN)
				.compassEnabled(false)
				.camera(new CameraPosition.Builder().target(melbournecenter)
						.zoom(15).build());

		// Create a new MapFragment to be placed in the fragment layout
		mapFragment = SupportMapFragment.newInstance(options);
		mapFragment.setRetainInstance(true);
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
			} else {
				// Initialize markers
				initMarkers();
			}
		}
		return true;
	}

	/**
	 * Add the markers to the map
	 */
	private void initMarkers() {
		Log.d("Map", "InitMarkers");
		// Center the camera on Melbourne
		CameraUpdate update = CameraUpdateFactory.newLatLng(melbourne
				.getCenter());
		map.moveCamera(update);

		// holding the markers pictures
		BitmapDescriptor[] icons = new BitmapDescriptor[20];
		for (int i = 0; i < 20; i++) {
			icons[i] = BitmapDescriptorFactory.defaultMarker(startHue
					+ (float) i / 20 * (endHue - startHue));
		}

		Random rnd = new Random();
		// Instantiates a new CircleOptions object and defines the radius in
		// meters
		CircleOptions circleOptions = new CircleOptions().radius(20)
				.strokeColor(0x00000000);

		float[] hsv = new float[3];
		hsv[1] = 255f;
		hsv[2] = 255f;

		int sqrtnum = (int) Math.sqrt(numMarkers);
		for (int x = 0; x < sqrtnum; x++) {
			for (int y = 0; y < sqrtnum; y++) {
				float relativeStrength = rnd.nextFloat();
				float signalStrength = relativeStrength * -20 - 80;
				LatLng position = new LatLng(southwest.latitude
						+ ((float) x / sqrtnum) * lat, southwest.longitude
						+ ((float) y / sqrtnum) * lng);

				hsv[0] = startHue + relativeStrength * (endHue - startHue);
				circleOptions.center(position).fillColor(Color.HSVToColor(hsv));

				// map.addCircle(circleOptions);

				map.addMarker(new MarkerOptions().position(position)
						.icon(icons[(int) (20 * relativeStrength)])
						.title("Mesure")
						.snippet("SignalStrength : " + signalStrength + " DBm"));
			}
		}
	}
}
