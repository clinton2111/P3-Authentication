package com.p3authentication.tutorial;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.p3authentication.R;

public class TutorialFragmentC extends Fragment implements OnClickListener {

	TextView Fragment_Heading, Fragment_Content;
	BootstrapButton OK;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.tutorial_frag_c, container, false);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Fragment_Heading = (TextView) getView().findViewById(
				R.id.frag_C_heading);
		Fragment_Content = (TextView) getView().findViewById(
				R.id.frag_C_content);
		OK = (BootstrapButton) getView().findViewById(R.id.b_ok);
		OK.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.b_ok:
			SharedPreferences getprefs = PreferenceManager
					.getDefaultSharedPreferences(getActivity()
							.getApplicationContext());
			Editor edit = getprefs.edit();
			edit.putString("tut_launch", "no");
			edit.commit();
			getActivity().finish();
			break;
		}

	}
}
