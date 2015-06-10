package com.p3authentication.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.p3authentication.R;
import com.p3authentication.DBhandlerAndParsers.DataBaseHandler;

public class ApkAdapter extends ArrayAdapter<ApplicationInfo> {
	private List<ApplicationInfo> original_items;
	private List<ApplicationInfo> filtered_items;
	private Context context;
	private PackageManager packageManager;
	private AppFilter filter;
	DataBaseHandler handler;
	String[] LockedApps;
	Typeface fontFamily = Typeface.createFromAsset(getContext().getAssets(),
			"fontawesome-webfont.ttf");

	public ApkAdapter(Context context, int textViewResourceId,
			List<ApplicationInfo> appsList) {
		super(context, textViewResourceId, appsList);
		this.context = context;
		this.original_items = new ArrayList<ApplicationInfo>(appsList);
		this.filtered_items = new ArrayList<ApplicationInfo>(appsList);
		packageManager = context.getPackageManager();
		handler = new DataBaseHandler(getContext());
		try {
			handler.open();
			LockedApps = handler.getPackages();
			handler.close();
		} catch (Exception E) {
			LockedApps = null;

		}
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new AppFilter();
		}
		return filter;
	}

	@Override
	public int getCount() {
		return ((null != filtered_items) ? filtered_items.size() : 0);
	}

	@Override
	public ApplicationInfo getItem(int position) {
		return ((null != filtered_items) ? filtered_items.get(position) : null);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public boolean isEnabled(int position) {
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (null == view) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.apklist_item, null);
		}

		final ApplicationInfo data = filtered_items.get(position);
		if (position % 2 == 0) {
			view.setBackgroundColor(Color.parseColor("#fafafa"));
		} else {
			view.setBackgroundColor(Color.parseColor("#eeeeee"));
		}
		if (null != data) {

			final TextView appName = (TextView) view
					.findViewById(R.id.app_name);
			TextView packageName = (TextView) view
					.findViewById(R.id.app_package);
			final TextView lockstatus = (TextView) view
					.findViewById(R.id.lock_status);
			ImageView iconview = (ImageView) view.findViewById(R.id.app_icon);
			appName.setTextColor(Color.BLACK);
			appName.setText(data.loadLabel(packageManager));
			packageName.setText(data.packageName);
			lockstatus.setTypeface(fontFamily);
			try {
				if (Arrays.asList(LockedApps).contains(
						data.packageName.toString())) {
					lockstatus.setText("\uf205");

				} else if (LockedApps == null) {
					lockstatus.setText("\uf204");

				} else {
					lockstatus.setText("\uf204");
				}
			} catch (Exception E) {
				lockstatus.setText("\uf204");
			}

			lockstatus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) { // TODO Auto-generated method stub

					if (lockstatus.getText().toString().equals("\uf204")) {
						handler.open();
						handler.insertPackage(data.packageName.toString());
						LockedApps = handler.getPackages();
						handler.close();
						Toast.makeText(
								getContext(),
								appName.getText().toString()
										+ " has been locked",
								Toast.LENGTH_SHORT).show();
						lockstatus.setText("\uf205");
					} else if (lockstatus.getText().toString().equals("\uf205")) {
						handler.open();
						handler.deletePackage("'" + data.packageName.toString()
								+ "'");
						LockedApps = handler.getPackages();
						handler.close();
						Toast.makeText(
								getContext(),
								appName.getText().toString()
										+ " has been unlocked",
								Toast.LENGTH_SHORT).show();
						lockstatus.setText("\uf204");
					}
				}
			});

			iconview.setImageDrawable(data.loadIcon(packageManager));
		}
		return view;
	}

	private class AppFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub
			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (constraint != null && constraint.toString().length() > 0) {
				List<ApplicationInfo> filteredItems = new ArrayList<ApplicationInfo>();
				List<ApplicationInfo> list = new ArrayList<ApplicationInfo>(
						original_items);
				for (int i = 0; i < list.size(); i++) {
					ApplicationInfo data = list.get(i);
					String name = data.loadLabel(packageManager).toString();
					if (name.toLowerCase().contains(constraint)) {
						filteredItems.add(data);
					}
				}
				result.count = filteredItems.size();
				result.values = filteredItems;
			} else {
				synchronized (this) {
					List<ApplicationInfo> list = new ArrayList<ApplicationInfo>(
							original_items);
					result.values = list;
					result.count = list.size();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// TODO Auto-generated method stub
			filtered_items = (ArrayList<ApplicationInfo>) results.values;
			notifyDataSetChanged();
			clear();
			for (int i = 0; i < filtered_items.size(); i++)
				add(filtered_items.get(i));
			notifyDataSetInvalidated();
		}

	}
};