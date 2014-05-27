package com.example.itpappchallenge;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Place implements Parcelable {

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

	public Place(Parcel in) {
		readFromParcel(in);
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

	public LatLng getLatLng() {
		return new LatLng(latitude, longitude);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(checkin_count);
		dest.writeByte((byte) (isPublic ? 0x01 : 0x00));
		dest.writeString(address);
		dest.writeString(zip);
		dest.writeString(city);
		dest.writeLong(created_at);
		dest.writeDouble(distance);
	}

	private void readFromParcel(Parcel in) {
	    id = in.readInt();
        name = in.readString();
        checkin_count = in.readInt();
        isPublic = in.readByte() != 0x00;
        address = in.readString();
        zip = in.readString();
        city = in.readString();
        created_at = in.readLong();
        distance = in.readDouble();
	}

	public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
		public Place createFromParcel(Parcel in) {
			return new Place(in);
		}

		public Place[] newArray(int size) {
			return new Place[size];
		}
	};
}