package com.p3authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.p3authentication.R;
import com.p3authentication.DBhandlerAndParsers.DataBaseHandler;

public class PreReg extends Activity implements OnClickListener {
	BootstrapButton yes, no;
	TextView excl;
	Typeface fontFamily;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pre_reg);
		fontFamily = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "fontawesome-webfont.ttf");
		excl = (TextView) findViewById(R.id.textExcl);
		yes = (BootstrapButton) findViewById(R.id.b_yes);
		no = (BootstrapButton) findViewById(R.id.b_no);
		yes.setOnClickListener(this);
		no.setOnClickListener(this);
		excl.setTypeface(fontFamily);
		excl.setText("\uf071");

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.b_yes:
			DataBaseHandler handler = new DataBaseHandler(
					getApplicationContext());
			handler.open();
			handler.deletetable();
			handler.close();
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			Editor edit = sp.edit();
			edit.putInt("count", 0);
			edit.commit();
			startActivity(new Intent(getApplicationContext(), ImageGrid.class));
			finish();
			break;
		case R.id.b_no:
			finish();
			break;
		}
	}
}
