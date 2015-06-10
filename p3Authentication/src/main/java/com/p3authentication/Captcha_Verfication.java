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

public class Captcha_Verfication extends Activity {
	private static final int REQ_ENTER_PATTERN = 2;
	int retryCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Settings.Display.setCaptchaWiredDots(getApplicationContext(), 6);
		Intent intent = new Intent(LockPatternActivity.ACTION_VERIFY_CAPTCHA,
				null, getApplicationContext(), LockPatternActivity.class);
		startActivityForResult(intent, REQ_ENTER_PATTERN);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_ENTER_PATTERN: {

			switch (resultCode) {
			case RESULT_OK:
				Toast.makeText(getApplicationContext(), "Valid User",
						Toast.LENGTH_LONG).show();
				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(Captcha_Verfication.this);
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
				Toast.makeText(getApplicationContext(), "Incorrect",
						Toast.LENGTH_LONG).show();
				break;
			}

			/*
			 * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
			 * the number of tries that the user did.
			 */
			retryCount = data.getIntExtra(
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
