package com.example.itpappchallenge;

import com.example.itpappchallenge.MapFragment.OnMapActionsListener;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity implements
		OnMapActionsListener {

	private MapFragment map;
	private AddPlaceFragment addPlace;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		map = new MapFragment();
		
		FragmentManager fragmentManager = getSupportFragmentManager();
	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
	    fragmentTransaction.replace(android.R.id.content, map, "mapfragment_tag");
	    fragmentTransaction.commit();
	}

	@Override
	public void onAddPlaceClicked() {
		// Show 'add place' fragment
		addPlace = new AddPlaceFragment();
		
		FragmentManager fragmentManager = getSupportFragmentManager();
	    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
	    fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
	    fragmentTransaction.replace(android.R.id.content, addPlace, "addplace_fragment");
	    fragmentTransaction.addToBackStack(null);
	    fragmentTransaction.commit();
	}
}
