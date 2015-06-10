package com.p3authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.haibison.android.lockpattern.LockPatternActivity;
import com.p3authentication.DBhandlerAndParsers.DataBaseHandler;

public class Create_Pattern extends Activity {
	private static final int REQ_CREATE_PATTERN = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// This is your preferred flag
		Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN,
				null, getBaseContext(), LockPatternActivity.class);
		startActivityForResult(intent, REQ_CREATE_PATTERN);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case REQ_CREATE_PATTERN: {
			if (resultCode == RESULT_OK) {
				char[] pattern = data
						.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
				DataBaseHandler handler = new DataBaseHandler(this);
				handler.open();

				String PatternToWrite = new String(pattern);
				handler.createPattern(PatternToWrite);
				handler.close();
				Log.d("DEBUG", new String(pattern));

				Toast.makeText(getApplicationContext(), "Pattern Recorded",
						Toast.LENGTH_LONG).show();
				NavUtils.navigateUpFromSameTask(this);
				finish();

			}
			if (resultCode == RESULT_CANCELED) {
				NavUtils.navigateUpFromSameTask(this);
				finish();

			}
			break;
		}// REQ_CREATE_PATTERN

		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
