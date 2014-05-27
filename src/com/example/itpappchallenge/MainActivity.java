package com.example.itpappchallenge;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {

	private MapFragment map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			map = new MapFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(android.R.id.content, map, "mapfragment_tag")
					.commit();
		} else {
			map = (MapFragment) getSupportFragmentManager()
					.findFragmentByTag("mapfragment_tag");
		}
	}
}
