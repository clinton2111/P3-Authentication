package com.p3authentication;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageGrid extends Activity {
	DisplayImageOptions options;
	GridView gridview;
	String[] imageUrls;
	TextView imagesleft;
	ImageLoaderConfiguration config;
	int redirect, limit;
	SharedPreferences sp;
	boolean status = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.fr_image_grid);
		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String temp = sp.getString("ImageSeqNo", "4");
		limit = Integer.parseInt(temp);
		redirect = sp.getInt("count", 0);
		File Path = ImageGrid.this.getExternalFilesDir(null);
		Constants cons = new Constants(this);
		imageUrls = cons.getImagePath(Path.toString());
		config = new ImageLoaderConfiguration.Builder(ImageGrid.this).build();
		ImageLoader.getInstance().init(config);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		imagesleft = (TextView) findViewById(R.id.tv_pics_left);
		imagesleft.setText(redirect + "/" + limit + " images have been chosen");
		gridview = (GridView) findViewById(R.id.grid);
		gridview.setAdapter(new ImageAdapter());
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startImagePagerActivity(position);
			}
		});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		redirect = sp.getInt("count", 0);
		imagesleft.setText(redirect + "/" + limit + " images have been chosen");
		if (redirect >= limit) {
			finish();

		}

	}

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(ImageGrid.this, SingleViewActivity.class);
		intent.putExtra("position", position);
		startActivity(intent);
	}

	public class ImageAdapter extends BaseAdapter {
		public int getCount() {
			return imageUrls.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageLoader imageLoader = ImageLoader.getInstance();
			LayoutInflater inflater = LayoutInflater.from(ImageGrid.this);
			View view = convertView;
			final ViewHolder gridViewImageHolder;
			if (convertView == null) {
				view = inflater
						.inflate(R.layout.item_grid_image, parent, false);
				gridViewImageHolder = new ViewHolder();
				gridViewImageHolder.imageView = (ImageView) view
						.findViewById(R.id.image);
				gridViewImageHolder.progressBar = (ProgressBar) view
						.findViewById(R.id.progress);
				view.setTag(gridViewImageHolder);
			} else {
				gridViewImageHolder = (ViewHolder) view.getTag();
			}
			imageLoader.displayImage("file://" + imageUrls[position],
					gridViewImageHolder.imageView, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							gridViewImageHolder.progressBar.setProgress(0);
							gridViewImageHolder.progressBar
									.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							gridViewImageHolder.progressBar
									.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							gridViewImageHolder.progressBar
									.setVisibility(View.GONE);
						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri,
								View view, int current, int total) {
							gridViewImageHolder.progressBar.setProgress(Math
									.round(100.0f * current / total));
						}
					});

			return view;
		}

	}

	static class ViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			boolean check = ImageCheck();
			if (check == true) {
				NavUtils.navigateUpFromSameTask(this);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean ImageCheck() {
		if (redirect < limit) {

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					ImageGrid.this);
			alertDialogBuilder
					.setTitle("Warning")
					.setMessage(
							"You have chosen only "
									+ redirect
									+ "/"
									+ limit
									+ " Passpoints.Please select the remaining "
									+ (limit - redirect) + " Passpoint(s).")
					.setCancelable(false);
			alertDialogBuilder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					status = false;
					dialog.dismiss();

				}
			});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();

		}
		return status;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		boolean check = ImageCheck();
		if (check == true) {
			super.onBackPressed();
		}
	}
}
