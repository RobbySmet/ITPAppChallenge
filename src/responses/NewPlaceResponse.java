package responses;

import com.example.itpappchallenge.Place;

public class NewPlaceResponse {

	private Place place;
	private boolean success;

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
