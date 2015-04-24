package com.p3authentication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.p3authentication.DBhandlerAndParsers.DataBaseHandler;
import com.p3authentication.DBhandlerAndParsers.JSONParser;

public class Retrive_Password extends Activity implements OnClickListener {
	BootstrapButton Ok, Cancel, Check;
	BootstrapEditText email, Pin;
	String E_PIN = null;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	private String _URL;
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	String text = null;
	// flag for Internet connection status
	Boolean isInternetPresent = false, serverconnect = false;

	// Connection detector class
	Detector cd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.password_retrive);
		_URL = getResources().getString(R.string.base_URL) + "send_pin.php";
		email = (BootstrapEditText) findViewById(R.id.et_email);
		Ok = (BootstrapButton) findViewById(R.id.OK);
		cd = new Detector(getApplicationContext());
		Cancel = (BootstrapButton) findViewById(R.id.cancel);
		Check = (BootstrapButton) findViewById(R.id.b_check);
		Pin = (BootstrapEditText) findViewById(R.id.et_pin);
		Ok.setOnClickListener(this);
		Cancel.setOnClickListener(this);
		Check.setOnClickListener(this);
	}

	class SendPin extends AsyncTask<String, String, String> {
		boolean failure = false;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			pDialog = new ProgressDialog(Retrive_Password.this);
			pDialog.setMessage("Sending Message");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub

			String pin = text;
			String email_id = email.getText().toString();
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("pin", pin));
				params.add(new BasicNameValuePair("email_id", email_id));
				Log.d("request!", "starting");
				JSONObject json = jsonParser.makeHttpRequest(_URL, "POST",
						params);
				Log.d("Send attempt", json.toString());
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("Mail Sent!", json.toString());

					return json.getString(TAG_MESSAGE);
				} else {
					Log.d("Failure!", json.getString(TAG_MESSAGE));
					return json.getString(TAG_MESSAGE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if (result != null) {

				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_LONG).show();

			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.OK:
			if (email.getText().toString().equals("")) {
				email.setDanger();
				Toast.makeText(getBaseContext(),
						"Please enter a valid email id.", Toast.LENGTH_SHORT)
						.show();
			} else {
				email.setSuccess();
				Random r = new Random();
				int ran = (r.nextInt(9999 - 1000) + 1000);
				text = String.valueOf(ran);
				E_PIN = md5(text);
				isInternetPresent = cd.isConnectingToInternet();
				Executors.newSingleThreadExecutor().submit(new Runnable() {
					@Override
					public void run() {
						// You can performed your task here.
						serverconnect = cd.isConnectedToServer();
					}
				});
				if ((isInternetPresent == true) && (serverconnect == true)) {
					new SendPin().execute();
				} else {
					Toast.makeText(
							getApplicationContext(),
							"Please check your Internet connection or try again later.",
							Toast.LENGTH_LONG).show();
				}
			}

			break;
		case R.id.cancel:

			finish();

			break;
		case R.id.b_check:
			String RetString = null;
			if (Pin.getText().toString().equals("")) {
				Pin.setDanger();
				Toast.makeText(getBaseContext(),
						"Please enter the pin sent to your email id.",
						Toast.LENGTH_SHORT).show();
			}

			else {
				RetString = md5(Pin.getText().toString());
				if (RetString.equals(E_PIN)) {
					Toast.makeText(getApplicationContext(), "Successful",
							Toast.LENGTH_SHORT).show();
					DataBaseHandler handler = new DataBaseHandler(this);
					handler.open();
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(Retrive_Password.this);
					Editor edit = sp.edit();
					edit.putInt("count", 0);
					edit.commit();
					String redirect_class = sp.getString("reset_", "anon");
					if (redirect_class.equals("picture")) {
						handler.deletetable();
						startActivity(new Intent(getApplicationContext(),
								ImageGrid.class));
					} else if (redirect_class.equals("pattern")) {
						handler.deletePatterntable();
						startActivity(new Intent(getApplicationContext(),
								Create_Pattern.class));
					}
					handler.close();
					finish();
				} else {
					Toast.makeText(getBaseContext(), "Unsuccessful",
							Toast.LENGTH_SHORT).show();
				}
			}

			break;
		}
	}

	private String md5(String in) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(in.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);
			for (int i = 0; i < len; i++) {
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
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
