package com.p3authentication.service;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;

import com.p3authentication.Locker;
import com.p3authentication.DBhandlerAndParsers.DataBaseHandler;

public class LockerService extends Service {
	int pp_count, package_count;
	String LockedApps[];
	String allowedapp = null, limit_temp, packname, activityname;
	DataBaseHandler handler;
	Intent pwdIntent = null;
	ActivityManager am;
	Boolean data_status;
	ExecutorService executorService;
	LockerThread LcThread;
	boolean running_status;
	Editor edit;
	String[] AppActivities = { "com.p3authentication.Locker",
			"com.p3authentication.Compare_Pattern",
			"com.p3authentication.Captcha_Verfication",
			"com.haibison.android.lockpattern.LockPatternActivity" };
	SharedPreferences sp;
	private final static Handler servicehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

		}
	};

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		handler = new DataBaseHandler(this);
		am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		pwdIntent = new Intent(LockerService.this, Locker.class);
		pwdIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		sp = PreferenceManager.getDefaultSharedPreferences(LockerService.this);
		edit = sp.edit();

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		running_status = true;
		executorService = Executors.newSingleThreadExecutor();
		servicehandler.removeCallbacks(LcThread);
		LcThread = new LockerThread();
		executorService.submit(LcThread);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (executorService != null) {
			executorService.shutdown();
		}
		running_status = false;
		servicehandler.removeCallbacks(LcThread);
		stopSelf();
	}

	class LockerThread implements Runnable {

		@Override
		public void run() {
			while (running_status) {
				limit_temp = sp.getString("ImageSeqNo", "4");
				handler.open();
				LockedApps = handler.getPackages();
				package_count = handler.checkdata("PACKAGE");
				pp_count = handler.checkdata("USER");
				handler.close();
				if ((package_count > 0)
						&& (pp_count >= Integer.parseInt(limit_temp))) {
					packname = am.getRunningTasks(1).get(0).topActivity
							.getPackageName();
					activityname = am.getRunningTasks(1).get(0).topActivity
							.getClassName();

					allowedapp = sp.getString("allowedapp",
							"com.p3authentication");
					// Log.i("activity name", activityname);
					if ((packname.equals("com.p3authentication"))
							&& (allowedapp.equals("com.p3authentication"))) {
						// do nothing
					} else if ((packname.equals("com.p3authentication"))
							&& !(Arrays.asList(AppActivities)
									.contains(activityname))) {

						try {
							edit.putString("current_app", packname);
							edit.commit();
							startActivity(pwdIntent);
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if ((Arrays.asList(LockedApps).contains(packname))
							&& (allowedapp.equals(packname))) {
						// do nothing
					} else if ((Arrays.asList(LockedApps).contains(packname))) {
						edit.putString("current_app", packname);
						edit.commit();
						startActivity(pwdIntent);
					}
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}
}