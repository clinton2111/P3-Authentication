package com.p3authentication.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.p3authentication.R;

public class TutorialFragmentB extends Fragment {
	TextView Fragment_Heading, Fragment_Content;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.tutorial_frag_b, container, false);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Fragment_Heading = (TextView) getView().findViewById(
				R.id.frag_B_heading);
		Fragment_Content = (TextView) getView().findViewById(
				R.id.frag_B_content);
	}
}
