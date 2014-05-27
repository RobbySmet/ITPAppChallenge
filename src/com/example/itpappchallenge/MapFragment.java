package com.example.itpappchallenge;

import java.util.ArrayList;
import java.util.TreeMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.example.itpappchallenge.LocationHelper.LocationCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements LocationCallback, OnMarkerClickListener {
	// Parameters to send
	private static final String DISTANCE_PARAM = "distance";
	private static final String LONGITUDE_PARAM = "longitude";
	private static final String LATITUDE_PARAM = "latitude";

	protected static final String TAG = MainActivity.class.getSimpleName();

	private Context mContext;

	private LocationHelper locationHelper;
	private ProgressDialog progress;
	private double latitude;
	private double longitude;

	private static View view;
	private static GoogleMap mMap;

	private OnMapActionsListener mCallback;

	public interface OnMapActionsListener {
		public void onAddPlaceClicked();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mCallback = (OnMapActionsListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnMapActionsListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getActivity();
		setHasOptionsMenu(true);
		getPlacesNearby();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.map_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_place:
			mCallback.onAddPlaceClicked();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		view = (RelativeLayout) inflater.inflate(R.layout.map_fragment,
				container, false);
		setUpMapIfNeeded();

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onActivityCreated(savedInstanceState);
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = ((SupportMapFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(
							R.id.location_map)).getMap();

			if (mMap != null)
				setUpMap();
		}
	}

	private void setUpMap() {
		mMap.setMyLocationEnabled(true);
		mMap.setOnMarkerClickListener(this);
	}

	private void getPlacesNearby() {
		progress = ProgressDialog.show(mContext,
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

			loadPlaces(latitude, longitude, 5);
			moveMapToCurrentLocation(latitude, longitude);
		}
	}

	private void moveMapToCurrentLocation(double latitude, double longitude) {
		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
				latitude, longitude));
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

		mMap.moveCamera(center);
		mMap.animateCamera(zoom);
	}

	private void loadPlaces(double latitude, double longitude, int distance) {
		// Signature
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put(LATITUDE_PARAM, Double.toString(latitude));
		params.put(LONGITUDE_PARAM, Double.toString(longitude));
		params.put(DISTANCE_PARAM, Integer.toString(distance));

		String signature = SignatureHelper.getSignature(params);

		// Request
		RequestQueue queue = VolleySingleton.getInstance(mContext)
				.getRequestQueue();

		String uri = String
				.format("http://challenge.itpservices.be/place?latitude=%s&longitude=%s&distance=%s&signature=%s",
						latitude, longitude, distance, signature);

		GsonRequest<PlacesResponse> req = new GsonRequest<PlacesResponse>(
				Method.GET, uri, PlacesResponse.class, createSuccessListener(),
				createErrorListener());

		queue.add(req);
	}

	private Listener<PlacesResponse> createSuccessListener() {
		return new Listener<PlacesResponse>() {

			@Override
			public void onResponse(PlacesResponse response) {
				progress.dismiss();

				if (response.isSuccess()) {
					addMarkersToMap(response.getPlaces());
				}
			}
		};
	}

	protected void addMarkersToMap(ArrayList<Place> places) {
		for (Place place : places) {
			LatLng location = new LatLng(place.getLatitude(),
					place.getLongitude());

			int iconResource;
			
			if(place.isPublic()){
				iconResource = R.drawable.ic_toilets_public;
			}else{
				iconResource = R.drawable.ic_toilets;
			}
			
			mMap.addMarker(new MarkerOptions()
					.position(location)
					.title(place.getName())
					.snippet(place.getAddress())
					.icon(BitmapDescriptorFactory.fromResource(iconResource)));
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return true;
	}

	private ErrorListener createErrorListener() {
		return new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				progress.dismiss();
				Toast.makeText(mContext, getString(R.string.error),
						Toast.LENGTH_LONG).show();
			}
		};
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mMap = null;
	}
}
