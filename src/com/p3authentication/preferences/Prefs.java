package com.p3authentication.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.p3authentication.Detector;
import com.p3authentication.R;
import com.p3authentication.Splashscreen;
import com.p3authentication.DBhandlerAndParsers.DataBaseHandler;
import com.p3authentication.service.NumberListerner;
import com.p3authentication.service.Startup;

@SuppressLint("ClickableViewAccessibility")
public class Prefs extends PreferenceActivity implements
		OnPreferenceClickListener, OnTouchListener {
	final ComponentName LAUNCHER_COMPONENT_NAME = new ComponentName(
			"com.p3authentication", "com.p3authentication.Launcher");
	EditTextPreference AccessPin;
	List<Float> CalList;
	TextView fingsize, Largest;
	Float LargeValue = 0f;
	ImageView calibrator;
	private Queue<Long> enqueue;
	Detector cd;
	Boolean isInternetPresent = false, serverconnect = false;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		addPreferencesFromResource(R.xml.pref);

		AccessPin = (EditTextPreference) getPreferenceManager().findPreference(
				"KeypadKey");
		final Preference ResetPreference = getPreferenceManager()
				.findPreference("reset_data");
		final Preference EULAPreference = getPreferenceManager()
				.findPreference("eula");
		final Preference AboutUsPreference = getPreferenceManager()
				.findPreference("about_us");
		final Preference UnlockAppsPreference = getPreferenceManager()
				.findPreference("unlock_apps");
		final Preference TouchTolerance = getPreferenceManager()
				.findPreference("touch_tolerance");
		final Preference RedownloadImage = getPreferenceManager()
				.findPreference("redownload_images");
		final Preference RateApp = getPreferenceManager().findPreference(
				"rate_app");
		final CheckBoxPreference GhostStatusPref = (CheckBoxPreference) getPreferenceManager()
				.findPreference("GhostStatus");
		final CheckBoxPreference StartServicePref = (CheckBoxPreference) getPreferenceManager()
				.findPreference("startup_service");
		final ListPreference patterntype = (ListPreference) getPreferenceManager()
				.findPreference("PatternType");
		final ListPreference PatternStealth = (ListPreference) getPreferenceManager()
				.findPreference("PatternStealth");
		if (patterntype.getValue().toString().equals("No Pattern")) {
			PatternStealth.setEnabled(false);
		} else {
			PatternStealth.setEnabled(true);
		}

		if (GhostStatusPref.isChecked()) {
			AccessPin.setEnabled(true);
		} else {
			AccessPin.setEnabled(false);
		}
		AccessPin
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub
						if (newValue.toString().trim().equals("")) {

							Toast.makeText(Prefs.this,
									"Your Custom PIN cannot be empty.",
									Toast.LENGTH_LONG).show();

							return false;
						} else if (newValue.toString().length() < 5) {
							Toast.makeText(
									Prefs.this,
									"Your Custom PIN must be atleast 5 Digits.",
									Toast.LENGTH_LONG).show();

							return false;
						}
						Toast.makeText(Prefs.this, "Your Custom PIN is set.",
								Toast.LENGTH_LONG).show();
						return true;
					}

				});
		GhostStatusPref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if (newValue.toString().equals("true")) {
							hideLauncherIcon();
							Toast.makeText(Prefs.this,
									"P3 hidden from App Menu",
									Toast.LENGTH_SHORT).show();
						} else {
							showLauncherIcon();
							Toast.makeText(Prefs.this,
									"P3 visible on App Menu",
									Toast.LENGTH_SHORT).show();
						}
						return true;
					}

				});
		StartServicePref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if (newValue.toString().equals("true")) {
							ComponentName receiver = new ComponentName(
									Prefs.this, Startup.class);
							PackageManager pm = getPackageManager();

							pm.setComponentEnabledSetting(
									receiver,
									PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
									PackageManager.DONT_KILL_APP);

						} else {
							ComponentName receiver = new ComponentName(
									Prefs.this, Startup.class);
							PackageManager pm = getPackageManager();

							pm.setComponentEnabledSetting(
									receiver,
									PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
									PackageManager.DONT_KILL_APP);
						}
						return true;
					}

				});
		patterntype
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub
						if (!newValue.toString().equals("No Pattern")) {
							PatternStealth.setEnabled(true);
						} else {
							PatternStealth.setEnabled(false);
						}
						return true;
					}
				});
		ResetPreference.setOnPreferenceClickListener(this);
		UnlockAppsPreference.setOnPreferenceClickListener(this);
		TouchTolerance.setOnPreferenceClickListener(this);
		RedownloadImage.setOnPreferenceClickListener(this);
		RateApp.setOnPreferenceClickListener(this);
		AboutUsPreference.setOnPreferenceClickListener(this);
		EULAPreference.setOnPreferenceClickListener(this);

	}

	protected void showLauncherIcon() {
		// TODO Auto-generated method stub
		Prefs.this.getPackageManager().setComponentEnabledSetting(
				LAUNCHER_COMPONENT_NAME,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
		ComponentName receiver = new ComponentName(Prefs.this,
				NumberListerner.class);
		PackageManager pm = Prefs.this.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
		AccessPin.setEnabled(false);
	}

	protected void hideLauncherIcon() {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new AlertDialog.Builder(Prefs.this);
		builder.setTitle("Important!");
		builder.setMessage("To launch P3 again, dial "
				+ AccessPin.getText().toString()
				+ " or set up your custom Pin below.");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Prefs.this.getPackageManager().setComponentEnabledSetting(
						LAUNCHER_COMPONENT_NAME,
						PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
						PackageManager.DONT_KILL_APP);

			}
		});
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.show();
		ComponentName receiver = new ComponentName(Prefs.this,
				NumberListerner.class);
		PackageManager pm = Prefs.this.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
		AccessPin.setEnabled(true);

	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		switch (preference.getKey()) {
		case "reset_data":
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					Prefs.this);
			alertDialogBuilder
					.setMessage("Are you sure you want to reset P3.All your crediantials will be reset and locked apps will no longer be locked. ");
			alertDialogBuilder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							DataBaseHandler handler = new DataBaseHandler(
									getApplicationContext());
							handler.open();
							handler.deletetable();
							handler.deletePatterntable();
							handler.deletePackageTable();
							handler.close();
							Toast.makeText(getApplicationContext(),
									"Data Cleared", Toast.LENGTH_LONG).show();

						}
					});
			alertDialogBuilder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			break;
		case "unlock_apps":
			AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(
					Prefs.this);
			alertDialogBuilder1
					.setMessage("Are you sure you want to unlock all your locked apps? ");
			alertDialogBuilder1.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							DataBaseHandler handler = new DataBaseHandler(
									getApplicationContext());
							handler.open();
							handler.deletePackageTable();
							handler.close();
							Toast.makeText(getApplicationContext(),
									"All Applications unlocked.",
									Toast.LENGTH_LONG).show();

						}
					});
			alertDialogBuilder1.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});

			AlertDialog alertDialog1 = alertDialogBuilder1.create();
			alertDialog1.show();
			break;
		case "touch_tolerance":
			tolerance_dialog();
			break;
		case "redownload_images":
			cd = new Detector(Prefs.this);
			isInternetPresent = cd.isConnectingToInternet();
			Executors.newSingleThreadExecutor().submit(new Runnable() {
				@Override
				public void run() {
					// You can performed your task here.
					serverconnect = cd.isConnectedToServer();
				}
			});
			if ((isInternetPresent == true) && (serverconnect == true)) {
				AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(
						Prefs.this);
				alertDialogBuilder2
						.setTitle("Warning")
						.setMessage(
								"This will Redownload all the images for P3.Please wait for the download to complete to Reset or Use the Passpoint feature.")
						.setCancelable(false);
				alertDialogBuilder2.setPositiveButton("OK",
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								redownload_images();
								dialog.dismiss();

							}
						});
				alertDialogBuilder2.setNegativeButton("Cancel",
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();

							}
						});
				AlertDialog alertDialog2 = alertDialogBuilder2.create();
				alertDialog2.show();

			} else {
				AlertDialog.Builder Abuilder = new Builder(Prefs.this);
				Abuilder.setMessage("Either you or the P3 servers are offline. Please check whether your are connected to the internet or try again later.");
				Abuilder.setPositiveButton("OK", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();

					}
				});
				final AlertDialog A1 = Abuilder.create();
				A1.show();
			}

			break;
		case "rate_app":
			final String appPackageName = getPackageName();
			try {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id=" + appPackageName)));
			} catch (android.content.ActivityNotFoundException E) {
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://play.google.com/store/apps/details?id="
								+ appPackageName)));
			}
			break;
		case "about_us":
			AlertDialog.Builder AboutUs_builder = new Builder(Prefs.this);
			AboutUs_builder.setTitle("About Us");
			AboutUs_builder.setMessage(Html
					.fromHtml(getString(R.string.aboutus)));
			AboutUs_builder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();

				}
			});
			final AlertDialog About_us = AboutUs_builder.create();
			About_us.show();

			break;
		case "eula":
			AlertDialog.Builder EULA_builder = new Builder(Prefs.this);
			EULA_builder.setTitle("End User License Agreement");
			EULA_builder.setMessage(Html.fromHtml(getString(R.string.EULA)));
			EULA_builder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();

				}
			});
			final AlertDialog EULA = EULA_builder.create();
			EULA.show();

			break;
		}
		return false;
	}

	private void redownload_images() {
		// TODO Auto-generated method stub
		File Path = getExternalFilesDir(null);
		String BASEURL = getResources().getString(R.string.base_URL);
		enqueue = new ConcurrentLinkedQueue<>();
		String[] imagenames = getResources().getStringArray(R.array.pic_name);
		boolean temp = Splashscreen
				.isDownloadManagerAvailable(getApplicationContext());
		for (int index = 0; index < imagenames.length; index++) {
			File image = new File(Path + "/" + imagenames[index]);
			if (image.exists()) {
				image.delete();
				if (temp == true) {
					downloadFile(BASEURL, imagenames[index]);
				}
			}
		}

	}

	public void downloadFile(String BASEURL, String imagename) {
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
		DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		enqueue.offer(dm.enqueue(request));

	}

	@SuppressWarnings("static-access")
	public void tolerance_dialog() {
		// TODO Auto-generated method stub
		final AlertDialog.Builder tol = new AlertDialog.Builder(Prefs.this);
		final LayoutInflater inflater = (LayoutInflater) Prefs.this
				.getSystemService(Prefs.this.LAYOUT_INFLATER_SERVICE);
		final View Viewlayout = inflater.inflate(R.layout.tolerance_dialog,
				(ViewGroup) findViewById(R.id.tolerance_layout));
		tol.setTitle("Please adjust the tolerance");
		tol.setView(Viewlayout);

		calibrator = (ImageView) Viewlayout.findViewById(R.id.iv_calibrator);
		fingsize = (TextView) Viewlayout.findViewById(R.id.tv_size);
		Largest = (TextView) Viewlayout.findViewById(R.id.tv_largest);
		CalList = new ArrayList<Float>();
		tol.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				int i = LargeValue.intValue();
				int screenWidth = (int) ((getWindowManager()
						.getDefaultDisplay().getWidth()) * 0.055);
				if (i > screenWidth) {
					PreferenceManager
							.getDefaultSharedPreferences(
									getApplicationContext()).edit()
							.putInt("tolerance", i).apply();
				}
				dialog.dismiss();
			}

		});
		tol.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		calibrator.setOnTouchListener(this);
		final AlertDialog tolDialog = tol.create();

		WindowManager.LayoutParams wmlp = tolDialog.getWindow().getAttributes();
		wmlp.gravity = Gravity.BOTTOM;
		tolDialog.show();

	}

	@SuppressWarnings("static-access")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		int N = event.getHistorySize();
		if (action == event.ACTION_DOWN) {
			calibrator.setColorFilter(Color.BLACK);
			for (int i = 0; i < N; i++) {
				calibrate(event.getHistoricalX(i), event.getHistoricalY(i),
						event.getHistoricalPressure(i),
						event.getHistoricalSize(i));
			}
			calibrate(event.getX(), event.getY(), event.getPressure(),
					event.getSize());
		} else if (action == event.ACTION_UP) {
			Float temp = Collections.max(CalList);
			temp = (float) Math.ceil((Double.parseDouble(Float.valueOf(temp)
					.toString())) / 10);
			LargeValue = temp * 10;
			calibrator.setColorFilter(Color.GREEN);
			// Largest.setText(String.valueOf(LargeValue));

		}

		return true;
	}

	@SuppressWarnings("deprecation")
	private void calibrate(float x, float y, float pressure, float size) {

		Display display = Prefs.this.getWindowManager().getDefaultDisplay();
		float mCurRadius = (size * (display.getWidth() / 3));
		// fingsize.setText(String.valueOf(mCurRadius));
		CalList.add(mCurRadius);
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
