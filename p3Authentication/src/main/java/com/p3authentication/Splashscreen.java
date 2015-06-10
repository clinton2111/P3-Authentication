package com.p3authentication;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ProgressBarCircularIndetermininate;

public class Splashscreen extends Activity {
	TextView downloadtext;
	ProgressBarCircularIndetermininate downloadprogress;
	String[] imagenames;
	String BASEURL;
	private Queue<Long> enqueue = new ConcurrentLinkedQueue<>();
	private DownloadManager dm = null;
	int counter;
	Detector cd;
	Boolean isInternetPresent = false, serverconnect = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		downloadtext = (TextView) findViewById(R.id.downloadtv);
		downloadprogress = (ProgressBarCircularIndetermininate) findViewById(R.id.download_progress);
		downloadprogress.setBackgroundColor(Color.parseColor("#fafafa"));
		BASEURL = getResources().getString(R.string.base_URL);
		imagenames = getResources().getStringArray(R.array.pic_name);
		cd = new Detector(Splashscreen.this);
		File Path = getExternalFilesDir(null);
		File noMedia = new File(Path + "/.nomedia");
		if (!noMedia.exists()) {
			try {
				noMedia.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Path.mkdirs();
		for (int index = 0; index < imagenames.length; index++) {
			File image = new File(Path + "/" + imagenames[index]);

			if (image.exists()) {
				downloadtext.setText("file exists");
				counter++;
				checkdata(counter);
			} else {
				isInternetPresent = cd.isConnectingToInternet();
				if (isInternetPresent) {
					Boolean result = isDownloadManagerAvailable(getApplicationContext());
					if (result) {
						downloadprogress.setVisibility(View.VISIBLE);
						downloadtext.setVisibility(View.VISIBLE);
						downloadtext.setText("Downloading Required Files");
						downloadFile(imagenames[index]);
					}

				} else {

					Toast.makeText(getApplicationContext(),
							"Please check your Internet connection.",
							Toast.LENGTH_LONG).show();
				}

			}

		}

	}

	private void checkdata(int counter) {
		// TODO Auto-generated method stub
		if (counter == imagenames.length) {

			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			finish();
			startActivity(i);

		}

	}

	@SuppressLint("NewApi")
	public void downloadFile(String imagename) {
		// TODO Auto-generated method stub
		String DownloadUrl = BASEURL + "images/" + imagename;
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(DownloadUrl));
		request.setDescription("P3 Resources"); // appears the same
												// in Notification
												// bar while
												// downloading
		request.setTitle("P3 Resources");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			request.allowScanningByMediaScanner();
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
		}
		String fileName = DownloadUrl.substring(
				DownloadUrl.lastIndexOf('/') + 1, DownloadUrl.length());
		request.setDestinationInExternalFilesDir(getApplicationContext(), null,
				fileName);

		// get download service and enqueue file
		dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		enqueue.offer(dm.enqueue(request));

	}

	public static boolean isDownloadManagerAvailable(Context context) {
		try {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setClassName("com.android.providers.downloads.ui",
					"com.android.providers.downloads.ui.DownloadList");
			List<ResolveInfo> list = context.getPackageManager()
					.queryIntentActivities(intent,
							PackageManager.MATCH_DEFAULT_ONLY);
			return list.size() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
				long downloadId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, 0);
				if (enqueue.contains(downloadId)) {
					enqueue.remove(downloadId);
					counter++;
				}

				if (!enqueue.isEmpty()) {
					return;
				}

				// not waiting on any more downloads
				checkdata(counter);
			}
		}
	};

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void onResume() {
		super.onResume();

		registerReceiver(receiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			unregisterReceiver(receiver);
		} catch (Exception E) {
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(receiver);
		} catch (Exception E) {
		}
	}

}
