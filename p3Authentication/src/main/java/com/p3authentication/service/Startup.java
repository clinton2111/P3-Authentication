package com.p3authentication.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Startup extends BroadcastReceiver {

	public Startup() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// start your service here

		context.startService(new Intent(context, LockerService.class));

	}

}