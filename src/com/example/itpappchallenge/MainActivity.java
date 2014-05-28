package com.example.itpappchallenge;

import java.util.ArrayList;
import java.util.TreeMap;

import Responses.PlacesResponse;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.example.itpappchallenge.AddPlaceFragment.onNewPlaceAddedListener;
import com.example.itpappchallenge.LocationHelper.LocationCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends ActionBarActivity implements OnMarkerClickListener, LocationCallback, OnBackStackChangedListener, onNewPlaceAddedListener {
	// Parameters to send
	private static final String DISTANCE_KEY = "distance";
	private static final String LONGITUDE_KEY = "longitude";
	private static final String LATITUDE_KEY = "latitude";
	
	private static final String ISLOADING_KEY = "IsLoading";

	protected static final String TAG = MainActivity.class.getSimpleName();

	private Context mContext;

	private LocationHelper locationHelper;
	private ProgressDialog progressDialog;
	
	// Default location = in the pocket
	private double latitude = 51.057511;
	private double longitude = 3.725170;

	private boolean isLoading;
	private GoogleMap mMap;
	private AddPlaceFragment addPlace;
	private RetainMapFragment mapFrag;
	private Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		setContentView(R.layout.activity_main);

		mapFrag = (RetainMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

		mMap = mapFrag.getMap();
		mMap.setMyLocationEnabled(true);
		mMap.setOnMarkerClickListener(this);

		if (savedInstanceState == null) {
			// Move to default location
			moveMapToLocation(latitude, longitude);
			
			getCurrentLocation();
		}else{
			// If screen was rotated while loading, restore state
			isLoading = savedInstanceState.getBoolean(ISLOADING_KEY);
		}
		
		// Reset title if user presses back from other fragment
		getSupportFragmentManager().addOnBackStackChangedListener(this);
	}
	
	@Override
	public void onBackStackChanged() {
		shouldDisplayHomeUp();
	}
	
	private void shouldDisplayHomeUp() {
		boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
		getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
		
		if (!canback){
        	getSupportActionBar().setTitle(getString(R.string.app_name));
        	menu.findItem(R.id.menu_add_place).setVisible(true);
        }
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		 getSupportFragmentManager().popBackStack();
		 return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(ISLOADING_KEY, isLoading);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		if (isLoading) {
			progressDialog.show();
		}
		super.onResume();
	}
	
	@Override
	public void onPause() {
		if (progressDialog != null && progressDialog.isShowing()) {
			isLoading = true;
			progressDialog.dismiss();
		} else {
			isLoading = false;
		}

		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_menu, menu);
	    
	    this.menu = menu;
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_place:
			showAddPlaceFragment();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showAddPlaceFragment() {
		// Show 'add place' fragment
		addPlace = new AddPlaceFragment();
		
		menu.findItem(R.id.menu_add_place).setVisible(false);

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		fragmentTransaction.replace(R.id.container, addPlace);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	private void getCurrentLocation() {
		progressDialog = ProgressDialog.show(mContext,
				getString(R.string.loading_toilets),
				getString(R.string.loading), true);

		locationHelper = new LocationHelper(mContext, this);
		locationHelper.connect(); // returns in onLocationClientConnected
	}

	@Override
	public void onLocationClientConnected() {
		// Location client is connected, we can start making request
		Location loc = locationHelper.getLastKnownLocation();

		if (loc != null) {
			latitude = loc.getLatitude();
			longitude = loc.getLongitude();

			getNearbyPlaces(latitude, longitude, 5);
			moveMapToLocation(latitude, longitude);
		}
	}

	private void moveMapToLocation(double latitude, double longitude) {
		// Zoom to default
		moveMapToLocation(latitude, longitude, 15);
	}
	
	private void moveMapToLocation(double latitude, double longitude, int zoomFactor) {
		if (mMap != null) {
			CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(zoomFactor);

			mMap.moveCamera(center);
			mMap.animateCamera(zoom);
		}
	}

	private void getNearbyPlaces(double latitude, double longitude, int distance) {
		// Signature
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put(LATITUDE_KEY, Double.toString(latitude));
		params.put(LONGITUDE_KEY, Double.toString(longitude));
		params.put(DISTANCE_KEY, Integer.toString(distance));

		String signature = SignatureHelper.getSignature(params);

		// Request
		RequestQueue queue = VolleySingleton.getInstance(mContext).getRequestQueue();

		String uri = String.format("http://challenge.itpservices.be/place?latitude=%s&longitude=%s&distance=%s&signature=%s",
						latitude, longitude, distance, signature);

		GsonRequest<PlacesResponse> req = new GsonRequest<PlacesResponse>(
				Method.GET, 
				uri, 
				PlacesResponse.class, 
				createSuccessListener(),
				createErrorListener());

		queue.add(req);
	}

	private Listener<PlacesResponse> createSuccessListener() {
		return new Listener<PlacesResponse>() {

			@Override
			public void onResponse(PlacesResponse response) {
				progressDialog.dismiss();

				if (response.isSuccess()) {
					addMarkersToMap(response.getPlaces());
				}
			}
		};
	}

	protected void addMarkersToMap(ArrayList<Place> places) {
		for (Place place : places) {
			addMarker(place);
		}
	}
	
	private void addMarker(Place place){
		int iconResource;

		if (place.isPublic()) {
			iconResource = R.drawable.ic_toilets_public;
		} else {
			iconResource = R.drawable.ic_toilets;
		}

		mMap.addMarker(new MarkerOptions().position(place.getLatLng())
				.title(place.getName()).snippet(place.getAddress())
				.icon(BitmapDescriptorFactory.fromResource(iconResource)));
	}

	private ErrorListener createErrorListener() {
		return new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				progressDialog.dismiss();
				Toast.makeText(mContext, getString(R.string.error),
						Toast.LENGTH_LONG).show();
			}
		};
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return true;
	}

	@Override
	public void newPlaceAdded(Place place) {
		addMarker(place);
		moveMapToLocation(place.getLatitude(), place.getLongitude());
		
		Toast.makeText(mContext, place.getName() + " toegevoegd", Toast.LENGTH_LONG).show();
	}
}
