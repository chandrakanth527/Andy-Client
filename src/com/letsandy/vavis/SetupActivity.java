package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SetupActivity extends Activity {
	TextView okTextView;
	ArrayList<String> URLs = new ArrayList<String>();
	int cur_pos = 0;
	int setup_status = 0;
	String final_gatewayIP;

	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.getconfigurationfiles);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);

		final EditText editTextView = (EditText) findViewById(R.id.editTextView);
		editTextView.setText("192.168.1.5");
		editTextView.setSelection("192.168.1.5".length());

		RelativeLayout okRelativeLayout = (RelativeLayout) findViewById(R.id.okRelativeLayout);
		okRelativeLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (setup_status == 0) {

					okTextView = (TextView) findViewById(R.id.okTextView);
					okTextView.setText("loading...");
					String gatewayIP = editTextView.getText().toString();
					String URL = "http://" + gatewayIP + "/readConfig/?filename=config.cfg";
					new getPackage().execute(URL);
					URLs.clear();
				} else {
					SharedPreferences mPrefs1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					SharedPreferences.Editor editor1 = mPrefs1.edit();
					editor1.putBoolean("setup_state", true);
					editor1.commit();

					SharedPreferences mPrefs2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					SharedPreferences.Editor editor2 = mPrefs2.edit();
					editor2.putString("gatewayIP", final_gatewayIP);
					editor2.commit();

					Intent i = new Intent(getBaseContext(), MainActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
			}
		});

	}

	@Override
	public void onBackPressed() {
	}

	public void writeFile(String FileName, String FileContent) {
		FileOutputStream outputStream;
		try {
			outputStream = openFileOutput(FileName, Context.MODE_PRIVATE);
			outputStream.write(FileContent.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void postHTTPExecute(String Response) {

		try {

			String configJSON = "configJSON.cfg";
			String rawConfigJSON = Response;
			writeFile(configJSON, rawConfigJSON);

			JSONObject objStatusJSON = new JSONObject(Response).getJSONObject("Options");
			Iterator<?> json_keys = objStatusJSON.keys();
			while (json_keys.hasNext()) {
				String json_key = (String) json_keys.next();
				String keyValue = objStatusJSON.getString(json_key);

				if (json_key.equals("VAVIS") && keyValue.equals("yes")) {
					String masterJSON = "masterJSON.cfg";
					URLs.add(masterJSON);

					String switchStatusJSON = "statusJSON.cfg";
					URLs.add(switchStatusJSON);

					String sceneJSON = "sceneJSON.cfg";
					String rawSceneJSON = "{\"Scene\":[]}";
					writeFile(sceneJSON, rawSceneJSON);

				} else if (json_key.equals("CCTV") && keyValue.equals("yes")) {

				} else if (json_key.equals("ZMOTE") && keyValue.equals("yes")) {
					String zmoteJSON = "zmoteJSON.cfg";
					URLs.add(zmoteJSON);

					String remotesJSON = "remotesJSON.cfg";
					// URLs.add(remotesJSON);
					String rawRemotesJSON = "{\"REMOTE\":[]}";
					writeFile(remotesJSON, rawRemotesJSON);

					String remoteControlsJSON = "remoteControlsJSON.cfg";
					URLs.add(remoteControlsJSON);

				} else if (json_key.equals("SECURITY") && keyValue.equals("yes")) {
				}
			}
			cur_pos++;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		new readURLs().execute();
	}

	public class getPackage extends AsyncTask<String, Void, String> {
		String server_response;
		String request;
		String status = "success";

		@Override
		protected String doInBackground(String... strings) {
			URL url;
			HttpURLConnection urlConnection = null;
			try {
				url = new URL(strings[0]);
				urlConnection = (HttpURLConnection) url.openConnection();
				int responseCode = urlConnection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					server_response = readStream(urlConnection.getInputStream());
				} else {
					status = "fail";
				}
			} catch (MalformedURLException e) {
				status = "fail";
			} catch (IOException e) {

				status = "fail";
			}
			return null;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			if (!status.equals("fail")) {
				postHTTPExecute(server_response);
			} else {
				Toast.makeText(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
				okTextView.setText("Retry");
			}
		}
	}

	public void loadCompleted(String status) {
		if (status.equals("success")) {
			Toast.makeText(getApplicationContext(), "Config fetch completed", Toast.LENGTH_SHORT).show();
			okTextView.setText("Finish & Launch");
			setup_status = 1;

		} else if (status.equals("fail")) {
			Toast.makeText(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
			okTextView.setText("Retry");
		}
	}

	public class readURLs extends AsyncTask<String, Void, String> {
		String server_response;
		String status = "success";

		@Override
		protected String doInBackground(String... strings) {

			Iterator<String> iter = URLs.iterator();
			String URL_NEW = null;
			try {

				while (iter.hasNext()) {

					String CFG = iter.next();
					final EditText editTextView = (EditText) findViewById(R.id.editTextView);
					String gatewayIP = editTextView.getText().toString();
					final_gatewayIP = gatewayIP;
					if (CFG.equals("remoteControlsJSON.cfg")) {
						URL_NEW = "http://" + gatewayIP + "/readRemote/";
					} else {
						URL_NEW = "http://" + gatewayIP + "/readConfig/?filename=" + CFG;
					}
					URL url = new URL(URL_NEW);
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setConnectTimeout(100);

					int responseCode = urlConnection.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK) {
						Log.e("success", CFG);
						server_response = readStream(urlConnection.getInputStream());
						writeFile(CFG, server_response);
					} else {
						Log.e("fail", CFG);
						status = "fail";
					}

				}
			} catch (MalformedURLException e) {
				status = "fail";
			} catch (IOException e) {
				status = "fail";
			}

			return null;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			loadCompleted(status);
		}
	}

	private String readStream(InputStream in) {
		BufferedReader reader = null;
		StringBuffer response = new StringBuffer();
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return response.toString();
	}
}
