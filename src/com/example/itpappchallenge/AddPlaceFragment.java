package com.example.itpappchallenge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class AddPlaceFragment extends Fragment {

	private EditText edtName;
	private EditText edtAddress;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_place_fragment, container, false);
		
		edtName = (EditText) view.findViewById(R.id.edt_name);
		edtAddress = (EditText) view.findViewById(R.id.edt_address);
		
		return view;
	}
}
