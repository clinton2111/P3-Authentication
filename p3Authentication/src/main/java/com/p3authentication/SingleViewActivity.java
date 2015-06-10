package com.p3authentication;

import java.io.File;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.p3authentication.R;
import com.p3authentication.DBhandlerAndParsers.DataBaseHandler;

public class SingleViewActivity extends Activity {
	int position;
	String[] imageUrls;
	ImageView touch;
	int touchHeight, touchWidth;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.single_view);
		File Path = getExternalFilesDir(null);
		Constants cons = new Constants(this);
		imageUrls = cons.getImagePath(Path.toString());
		// Get intent data
		Intent i = getIntent();
		touch = (ImageView) findViewById(R.id.touch);
		touchHeight = touch.getMeasuredHeight() / 2;
		touchWidth = touch.getMeasuredWidth() / 2;
		// Selected image id
		position = i.getExtras().getInt("position", -1);
		ImageView imageView = (ImageView) findViewById(R.id.SingleView);
		ImageLoader imageloader = ImageLoader.getInstance();
		imageloader.displayImage("file://" + imageUrls[position], imageView);
	}

	public boolean onTouchEvent(MotionEvent event) {
		// MotionEvent object holds X-Y values
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			final int XCood = (int) event.getX();
			final int YCood = (int) event.getY();

			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams.topMargin = YCood - (touchHeight);
			layoutParams.leftMargin = XCood - (touchWidth);
			touch.setLayoutParams(layoutParams);
			touch.setVisibility(View.VISIBLE);
			String imagepath = "file://" + imageUrls[position];
			String fileName = imagepath.substring(
					imagepath.lastIndexOf('/') + 1, imagepath.length());
			final String img = fileName;
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle("P3")
					.setMessage(
							"You have selected this point.Do you wish to continue?")
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									DataBaseHandler handler = new DataBaseHandler(
											SingleViewActivity.this);
									handler.open();
									String xcod = String.valueOf(XCood);
									String ycod = String.valueOf(YCood);
									handler.createEntry(xcod, ycod, img);
									handler.close();

									SharedPreferences sp = PreferenceManager
											.getDefaultSharedPreferences(SingleViewActivity.this);
									int redirect = sp.getInt("count", 0);
									redirect = redirect + 1;
									Editor edit = sp.edit();
									edit.putInt("count", redirect);
									edit.commit();
									finish();

								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
									touch.setVisibility(View.INVISIBLE);

								}
							});
			AlertDialog dialog = builder.create();
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			WindowManager.LayoutParams wmlp = dialog.getWindow()
					.getAttributes();
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int height = metrics.heightPixels;
			if ((height * 0.50) <= YCood) {
				wmlp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;

			} else if ((height * 0.50) > YCood) {
				wmlp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			}
			dialog.show();
		}
		return false;

	}
}
