package com.example.itpappchallenge;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class Place {

	private int id;
	private String name;
	private int checkin_count;
	
	@SerializedName("public")
	private boolean isPublic;
	private String address;
	private String zip;
	private String city;
	private long created_at;
	private double latitude, longitude;
	private double distance;
	
	public Place(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getCheckin_count() {
		return checkin_count;
	}

	public void setCheckin_count(int checkin_count) {
		this.checkin_count = checkin_count;
	}

	public Date getCreated_at() {
		return new Date(created_at);
	}

	public void setCreated_at(long created_at) {
		this.created_at = created_at;
	}
}