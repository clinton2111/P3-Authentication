package com.p3authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.haibison.android.lockpattern.LockPatternActivity;
import com.haibison.android.lockpattern.util.Settings;
import com.p3authentication.DBhandlerAndParsers.DataBaseHandler;

public class Compare_Pattern extends Activity {
	// This is your preferred flag
	private static final int REQ_ENTER_PATTERN = 2;
	SharedPreferences getprefs;
	char[] savedPattern;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DataBaseHandler handler = new DataBaseHandler(this);
		handler.open();
		String pat = handler.getPattern();
		handler.close();
		savedPattern = pat.toCharArray();
		getprefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String Stealth = getprefs.getString("PatternStealth", "No");
		if (Stealth.equals("Yes")) {
			Settings.Display.setStealthMode(getApplicationContext(), true);
		} else {
			Settings.Display.setStealthMode(getApplicationContext(), false);
		}
		Editor edit = getprefs.edit();
		edit.putString("reset_", "pattern");
		edit.commit();
		Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN,
				null, getBaseContext(), LockPatternActivity.class);
		Intent intentActivityForgotPattern = new Intent(Compare_Pattern.this,
				Retrive_Password.class);
		intent.putExtra(
				LockPatternActivity.EXTRA_INTENT_ACTIVITY_FORGOT_PATTERN,
				intentActivityForgotPattern);
		intent.putExtra(LockPatternActivity.EXTRA_PATTERN, savedPattern);
		startActivityForResult(intent, REQ_ENTER_PATTERN);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_ENTER_PATTERN: {
			/*
			 * NOTE that there are 4 possible result codes!!!
			 */
			switch (resultCode) {
			case RESULT_OK:
				// The user passed
				Toast.makeText(getApplicationContext(), "Valid User",
						Toast.LENGTH_LONG).show();
				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(Compare_Pattern.this);
				String current_app = sp.getString("current_app", "anon");
				Editor edit = sp.edit();
				edit.putString("allowedapp", current_app);
				edit.commit();
				finish();
				break;
			case RESULT_CANCELED:
				// The user cancelled the task
				finish();
				break;
			case LockPatternActivity.RESULT_FAILED:
				// The user failed to enter the pattern
				Toast.makeText(getApplicationContext(), "Failed",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
				break;
			case LockPatternActivity.RESULT_FORGOT_PATTERN:
				// The user forgot the pattern and invoked your recovery
				// Activity.
				finish();

				break;
			}

			/*
			 * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
			 * the number of tries that the user did.
			 */
			int retryCount = data.getIntExtra(
					LockPatternActivity.EXTRA_RETRY_COUNT, 0);

			break;
		}// REQ_ENTER_PATTERN
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

}
