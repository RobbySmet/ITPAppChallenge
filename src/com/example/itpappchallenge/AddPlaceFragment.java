package com.example.itpappchallenge;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import responses.NewPlaceResponse;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Required;

public class AddPlaceFragment extends Fragment implements OnClickListener, ValidationListener {

	private static final String NAME_KEY = "name";
	private static final String ADDRESS_KEY = "address";
	private static final String ZIP_KEY = "zip";
	private static final String CITY_KEY = "city";
	private static final String LATITUDE_KEY = "latitude";
	private static final String LONGITUDE_KEY = "longitude";
	private static final String ISPUBLIC_KEY = "public";
	private static final String SIGNATURE_KEY = "signature";
	
	@Required(order = 1, message = "Dit veld is verplicht.")
	private EditText edtName;
	@Required(order = 2, message = "Dit veld is verplicht.")
	private EditText edtAddress;
	@Required(order = 3, message = "Dit veld is verplicht.")
	private EditText edtZip;
	@Required(order = 4, message = "Dit veld is verplicht.")
	private EditText edtCity;
	private CheckBox cbxIsPublic;
	private Button btnSubmit;

	private onNewPlaceAddedListener callback;
	private Validator validator;

	public interface onNewPlaceAddedListener {
		public void newPlaceAdded(Place place);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			callback = (onNewPlaceAddedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement onNewPlaceAddedListener");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		validator = new Validator(this);
	    validator.setValidationListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_place_fragment, container,
				false);

		edtName = (EditText) view.findViewById(R.id.edt_name);
		edtAddress = (EditText) view.findViewById(R.id.edt_address);
		edtZip = (EditText) view.findViewById(R.id.edt_zipcode);
		edtCity = (EditText) view.findViewById(R.id.edt_city);
		cbxIsPublic = (CheckBox) view.findViewById(R.id.cbx_isPublic);
		btnSubmit = (Button) view.findViewById(R.id.btn_addplace);
		btnSubmit.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		getActionBar().setTitle(getString(R.string.add_place));
		super.onResume();
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.btn_addplace){
			 validator.validate();
		}		
	}

	@Override
	public void onValidationSucceeded() {
		 submit();
	}

	private void submit() {
		RequestQueue queue = VolleySingleton.getInstance(getActivity())
				.getRequestQueue();
		LatLng coordinates = getCoordinatesFromLocation(edtAddress.getText()
				.toString()
				+ ","
				+ edtZip.getText().toString()
				+ " "
				+ edtCity.getText().toString());

		// Signature
		final TreeMap<String, String> params = new TreeMap<String, String>();
		params.put(NAME_KEY, edtName.getText().toString());
		params.put(ADDRESS_KEY, edtAddress.getText().toString());
		params.put(ZIP_KEY, edtZip.getText().toString());
		params.put(CITY_KEY, edtCity.getText().toString());
		params.put(LATITUDE_KEY, Double.toString(coordinates.latitude));
		params.put(LONGITUDE_KEY, Double.toString(coordinates.longitude));
		params.put(ISPUBLIC_KEY, cbxIsPublic.isChecked() ? "true" : "false");

		String signature = SignatureHelper.getSignature(params);

		params.put(SIGNATURE_KEY, signature);

		GsonRequest<NewPlaceResponse> request = new GsonRequest<NewPlaceResponse>(
				Request.Method.POST, "http://challenge.itpservices.be/place",
				NewPlaceResponse.class, createResponseListener(),
				createErrorListener()) {
			@Override
			public Map<String, String> getParams() {
				return params;
			}
		};

		queue.add(request);
	}
	

	@Override
	public void onValidationFailed(View failedView, Rule<?> failedRule) {
		String message = failedRule.getFailureMessage();

        if (failedView instanceof EditText) {
            failedView.requestFocus();
            ((EditText) failedView).setError(message);
        } else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
	}
	
	private LatLng getCoordinatesFromLocation(String locationName) {
		Geocoder geocoder = new Geocoder(getActivity());
		List<Address> addresses;
		try {
			addresses = geocoder.getFromLocationName(locationName, 1);

			if (addresses.size() > 0) {
				double latitude = addresses.get(0).getLatitude();
				double longitude = addresses.get(0).getLongitude();

				return new LatLng(latitude, longitude);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private ErrorListener createErrorListener() {
		return new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getActivity(), getString(R.string.error),
						Toast.LENGTH_LONG).show();
			}
		};
	}

	private Listener<NewPlaceResponse> createResponseListener() {
		return new Listener<NewPlaceResponse>() {

			@Override
			public void onResponse(NewPlaceResponse response) {
				Place place = response.getPlace();
				callback.newPlaceAdded(place);
				
				getActivity().getSupportFragmentManager().popBackStack();
			}
		};
	}
}
