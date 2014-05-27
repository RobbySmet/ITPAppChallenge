package com.example.itpappchallenge;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

public class LocationHelper implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, 
		LocationListener {

	private String TAG = this.getClass().getSimpleName();
	private Context mContext;
	
	private LocationClient mLocationclient;
	
	// Callback
	public LocationCallback locationCallback;
	
	public interface LocationCallback{
		void onLocationClientConnected();
	}
	
	public LocationHelper(Context context, LocationCallback callback){
		mContext = context;
		locationCallback = callback;
	}
	
	public void connect() {
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
		if(resp == ConnectionResult.SUCCESS){
			mLocationclient = new LocationClient(mContext, this, this);
			mLocationclient.connect();
		}else{
			// use regular location classes
			Toast.makeText(mContext, "Google Play Service Error " + resp, Toast.LENGTH_LONG).show();
		}
	}
	
	public Location getLastKnownLocation(){
		if(mLocationclient != null && mLocationclient.isConnected()){
			Location loc = mLocationclient.getLastLocation();
			Log.i(TAG, "Last Known Location :" + loc.getLatitude() + "," + loc.getLongitude());
			return loc;
		}
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		locationCallback.onLocationClientConnected();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}
}
