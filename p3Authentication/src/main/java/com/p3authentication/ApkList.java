package com.p3authentication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ProgressBarCircularIndetermininate;
import com.p3authentication.DBhandlerAndParsers.DataBaseHandler;
import com.p3authentication.adapters.ApkAdapter;
import com.p3authentication.service.LockerService;

public class ApkList extends Fragment implements OnItemClickListener {
	ProgressBarCircularIndetermininate loadingprogress;
	PackageManager packageManager;
	private ListView lView;
	Context parent = null;
	ProgressDialog pDialog;
	List<ApplicationInfo> appInfos;
	View rootView;
	Switch service;
	ApkAdapter adapter;
	DataBaseHandler handler;
	int backgroundColor = Color.parseColor("#424242");
	ButtonFloat lockbutton;
	LinearLayout loadinglayout;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.app_list, container, false);

			lView = (ListView) rootView.findViewById(R.id.applist);
			service = (Switch) rootView.findViewById(R.id.switchService);
			lockbutton = (ButtonFloat) rootView.findViewById(R.id.LockFloat);
			lockbutton.setBackgroundColor(backgroundColor);
			lockbutton.setVisibility(View.GONE);

			lockbutton.setDrawableIcon(getResources().getDrawable(
					R.drawable.lock));
			loadingprogress = (ProgressBarCircularIndetermininate) rootView
					.findViewById(R.id.loading_progress);
			loadingprogress.setBackgroundColor(backgroundColor);
			loadinglayout = (LinearLayout) rootView
					.findViewById(R.id.loading_layout);
			loadinglayout.setVisibility(View.GONE);
			ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header,
					lView, false);
			lView.addHeaderView(header, null, false);

		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}
		return rootView;

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		setHasOptionsMenu(true);
		if (isMyServiceRunning()) {
			service.setChecked(true);
		} else {
			service.setChecked(false);
		}
		service.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getActivity().getBaseContext(),
						LockerService.class);
				if (service.isChecked()) {

					getActivity().startService(i);
					Toast.makeText(getActivity().getApplicationContext(),
							"Lock Enabled", Toast.LENGTH_SHORT).show();

				} else {

					getActivity().stopService(i);
					Toast.makeText(getActivity().getApplicationContext(),
							"Lock Disabled", Toast.LENGTH_SHORT).show();

				}
			}
		});

		new loadList().execute();
		lockbutton.setVisibility(View.VISIBLE);
		lockbutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				ApplicationInfo data;
				String packagename;
				lockbutton.setEnabled(false);
				handler = new DataBaseHandler(getActivity()
						.getApplicationContext());
				handler.open();
				handler.deletePackageTable();
				for (int index = 0; index < appInfos.size(); index++) {
					data = appInfos.get(index);
					packagename = data.packageName.toString();
					if (packagename.equals("com.p3authentication")) {
						continue;
					} else {
						handler.insertPackage(packagename);

					}
				}
				handler.close();
				final Intent intent = getActivity().getIntent();

				lockbutton.setEnabled(true);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setMessage(
						"All apps have been locked. To unlock all go to Settings -> Unlock all Applications.")
						.setCancelable(false)
						.setNegativeButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
										getActivity().finish();
										startActivity(intent);
									}
								});
				AlertDialog alert = builder.create();
				alert.show();

			}
		});

	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getActivity()
				.getSystemService(getActivity().ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.p3authentication.service.LockerService"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	class loadList extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			loadinglayout.setVisibility(View.VISIBLE);
			lockbutton.setEnabled(false);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			parent = getActivity().getBaseContext();
			// lView = (ListView) getView().findViewById(R.id.applist);
			final PackageManager packageManager = parent.getPackageManager();
			Intent intent = new Intent(Intent.ACTION_MAIN, null);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> resInfos = packageManager.queryIntentActivities(
					intent, 0);
			// using hashset so that there will be no duplicate packages,
			// if no duplicate packages then there will be no duplicate apps
			HashSet<String> packageNames = new HashSet<String>(0);
			appInfos = new ArrayList<ApplicationInfo>(0);
			// getting package names and adding them to the hashset
			for (ResolveInfo resolveInfo : resInfos) {
				if ((resolveInfo.activityInfo.packageName.toString())
						.equals("com.p3authentication")) {
					continue;
				} else {
					packageNames.add(resolveInfo.activityInfo.packageName);
				}
			}
			// now we have unique packages in the hashset, so get their
			// application infos and add them to the arraylist
			for (String packageName : packageNames) {
				try {
					appInfos.add(packageManager.getApplicationInfo(packageName,
							PackageManager.GET_META_DATA));
				} catch (NameNotFoundException e) {
					// Do Nothing
				}
			}
			Collections.sort(appInfos,
					new ApplicationInfo.DisplayNameComparator(packageManager));
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			adapter = new ApkAdapter(getActivity().getApplicationContext(),
					R.layout.apklist_item, appInfos);
			loadinglayout.setVisibility(View.GONE);
			lView.setAdapter(adapter);
			lockbutton.setEnabled(true);

		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.main, menu);
		SearchManager searchManager = (SearchManager) getActivity()
				.getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();

		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getActivity().getComponentName()));
		searchView.setIconifiedByDefault(true);
		searchView.setQueryHint("Search Applications");
		SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String newText) {
				// this is your adapter that will be filtered
				adapter.getFilter().filter(newText);
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				// this is your adapter that will be filtered
				adapter.getFilter().filter(query);
				return true;
			}

		};
		searchView.setOnQueryTextListener(textChangeListener);
	}

}
