package com.p3authentication;

import java.io.File;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.p3authentication.DBhandlerAndParsers.DataBaseHandler;

public class Locker extends Activity {
	int index = 0, limit, tolerance, state = 1;
	String[] bgimg;
	String[] ranimg;
	int[] ActXCod;
	int[] ActYCod;
	ImageView image;
	ImageLoader imageloader;
	File Path;
	ImageLoaderConfiguration config;
	SharedPreferences getprefs;
	String current_app;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.locker);
		config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.build();
		ImageLoader.getInstance().init(config);
		image = (ImageView) findViewById(R.id.imageView1);
		getprefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String temp = getprefs.getString("ImageSeqNo", "4");
		current_app = getprefs.getString("current_app", "anon");
		int Tolerance_temp = getprefs.getInt("tolerance", 0);
		limit = Integer.parseInt(temp);
		if (Tolerance_temp == 0) {
			Display display = getWindowManager().getDefaultDisplay();
			int screenWidth = display.getWidth();
			tolerance = (int) (screenWidth * 0.055);
		} else {
			tolerance = Tolerance_temp;
		}
		Path = getExternalFilesDir(null);
		Constants cons = new Constants(this);
		ranimg = cons.getImagePath(Path.toString());
		imageloader = ImageLoader.getInstance();
		try {
			// new Thread(new getData()).start();
			runOnUiThread(getData);

		} catch (Exception e) {
			Dialog d = new Dialog(this);
			d.setTitle("ERROR");
			TextView tv = new TextView(this);
			tv.setText("Opps! Something seems to have gone wrong. Did you setup your PassPoints?");
			d.setContentView(tv);
			d.show();
		}

	}

	public boolean onTouchEvent(MotionEvent event) {
		// MotionEvent object holds X-Y values
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			int XCood = (int) event.getX();
			int YCood = (int) event.getY();
			int distance = (int) Math.hypot(XCood - ActXCod[index], YCood
					- ActYCod[index]);

			/*
			 * String text = "You clicked at x = " + XCood + " and y = " +
			 * YCood; final Toast toast = Toast.makeText(this, text,
			 * Toast.LENGTH_SHORT); toast.show(); Handler handler = new
			 * Handler(); handler.postDelayed(new Runnable() {
			 * 
			 * @Override public void run() { toast.cancel(); } }, 750);
			 */

			if (index < (limit - 1)) // loop to check number of images
			{
				// loop to check status
				if ((state == 1) && (distance <= tolerance)) {
					index++;
					imageloader.displayImage("file://" + Path.toString() + "/"
							+ bgimg[index], image);
				} else {
					index++;
					Random r = new Random();
					int ran = r.nextInt(10 - 0);
					imageloader.displayImage("file://" + ranimg[ran], image);
					state = 0;
				}
			} else {
				if (state == 1) {
					String redirect = getprefs.getString("PatternType",
							"No Pattern");
					if (redirect.equals("No Pattern")) {

						Editor edit = getprefs.edit();
						edit.putString("allowedapp", current_app);
						edit.commit();
						Toast.makeText(getApplicationContext(), "Valid User",
								Toast.LENGTH_LONG).show();
						finish();
					} else if (redirect.equals("Pattern")) {
						Intent i = new Intent(Locker.this,
								Compare_Pattern.class);
						startActivity(i);
						finish();
					} else if (redirect.equals("CAPTCHA Pattern")) {
						Intent i = new Intent(Locker.this,
								Captcha_Verfication.class);
						startActivity(i);
						finish();
					}

				} else {
					new AlertDialog.Builder(this)
							.setTitle("Failed Login Attempt")
							.setMessage(
									"Your previous login attempt has failed.Would you like to try again?")
							.setPositiveButton(android.R.string.yes,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = getIntent();

											finish();
											startActivity(intent);
										}
									})
							.setNegativeButton(android.R.string.no,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											// do nothing
											finish();
										}
									}).show();
				}
			}

		}

		return super.onTouchEvent(event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.locker_actionbar, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_forgotPP:
			Editor edit = getprefs.edit();
			edit.putString("reset_", "picture");
			edit.commit();
			startActivity(new Intent(getApplicationContext(),
					Retrive_Password.class));
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private Runnable getData = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			DataBaseHandler handler = new DataBaseHandler(
					getApplicationContext());
			handler.open();
			ActXCod = handler.getXcod();
			ActYCod = handler.getYcod();
			bgimg = handler.getImg();
			handler.close();
			imageloader.displayImage("file://" + Path.toString() + "/"
					+ bgimg[index], image);

		}

	};

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
