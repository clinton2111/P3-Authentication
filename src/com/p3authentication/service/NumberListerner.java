package com.p3authentication.service;

import com.p3authentication.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class NumberListerner extends BroadcastReceiver {
	private String LAUNCHER_NUMBER;
	SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		LAUNCHER_NUMBER = sp.getString("KeypadKey", "12345");
		String phoneNubmer = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		if (LAUNCHER_NUMBER.equals(phoneNubmer)) {
			setResultData(null);
			Toast.makeText(context, "Number Pressed", Toast.LENGTH_LONG).show();
			Intent appIntent = new Intent(context, MainActivity.class);
			appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(appIntent);
		}
	}
}
