package com.example.itpappchallenge;

import java.util.ArrayList;

public class PlacesResponse {

	private ArrayList<Place> places;
	private boolean success;

	public ArrayList<Place> getPlaces() {
		return places;
	}

	public void setPlaces(ArrayList<Place> places) {
		this.places = places;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
