package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ConfigSyncActivity extends Activity {
	TextView okTextView;
	ArrayList<String> URLs = new ArrayList<String>();
	int cur_pos = 0;
	int setup_status = 0;
	int voice = 0;
	int cctv = 0;
	int zmote = 0;
	int vavis = 0;
	int scenes = 0;
	String type = "";

	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.configsync);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		final EditText editTextView = (EditText) findViewById(R.id.editTextView);
		editTextView.setText("192.168.1.5");
		editTextView.setSelection("192.168.1.5".length());

		final int resID1 = getResources().getIdentifier("ic_radio1", "drawable", this.getPackageName());
		final int resID2 = getResources().getIdentifier("ic_radio2", "drawable", this.getPackageName());
		String configJSON = "configJSON.cfg";
		String rawConfigJSON;
		JSONObject objStatusJSON;
		try {
			FileInputStream fis = openFileInput(configJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			rawConfigJSON = bufferedReader.readLine();
			objStatusJSON = new JSONObject(rawConfigJSON).getJSONObject("Options");
			if (objStatusJSON.has("VOICE") && objStatusJSON.getString("VOICE").equals("yes")) {
				LinearLayout voiceLinearLayout = (LinearLayout) findViewById(R.id.voiceLinearLayout);
				final ImageView voiceImageView = (ImageView) findViewById(R.id.voiceImageView);
				voiceLinearLayout.setVisibility(View.VISIBLE);
				voiceLinearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (voice == 0) {
							voice = 1;
							voiceImageView.setImageResource(resID1);
						} else {
							voiceImageView.setImageResource(resID2);
							voice = 0;
						}
					}

				});
			}
			if (objStatusJSON.has("VAVIS") && objStatusJSON.getString("VAVIS").equals("yes")) {
				LinearLayout switchesLinearLayout = (LinearLayout) findViewById(R.id.switchesLinearLayout);
				switchesLinearLayout.setVisibility(View.VISIBLE);
				final ImageView switchesImageView = (ImageView) findViewById(R.id.switchesImageView);
				switchesLinearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (vavis == 0) {
							vavis = 1;
							switchesImageView.setImageResource(resID1);
						} else {
							switchesImageView.setImageResource(resID2);
							vavis = 0;
						}
					}
				});
			}
			if (objStatusJSON.has("SCHEDULE") && objStatusJSON.getString("SCHEDULE").equals("yes")) {

			}
			if (objStatusJSON.has("SCENE") && objStatusJSON.getString("SCENE").equals("yes")) {
				LinearLayout scenesLinearLayout = (LinearLayout) findViewById(R.id.scenesLinearLayout);
				scenesLinearLayout.setVisibility(View.VISIBLE);

				final ImageView scenesImageView = (ImageView) findViewById(R.id.scenesImageView);
				scenesLinearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (scenes == 0) {
							scenes = 1;
							scenesImageView.setImageResource(resID1);
						} else {
							scenesImageView.setImageResource(resID2);
							scenes = 0;
						}
					}
				});
			}
			if (objStatusJSON.has("CCTV") && objStatusJSON.getString("CCTV").equals("yes")) {
				LinearLayout cctvLinearLayout = (LinearLayout) findViewById(R.id.cctvLinearLayout);
				cctvLinearLayout.setVisibility(View.VISIBLE);

				final ImageView cctvImageView = (ImageView) findViewById(R.id.cctvImageView);
				cctvLinearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (cctv == 0) {
							cctv = 1;
							cctvImageView.setImageResource(resID1);
						} else {
							cctvImageView.setImageResource(resID2);
							cctv = 0;
						}
					}
				});
			}
			if (objStatusJSON.has("ZMOTE") && objStatusJSON.getString("ZMOTE").equals("yes")) {
				LinearLayout remotesLinearLayout = (LinearLayout) findViewById(R.id.remotesLinearLayout);
				remotesLinearLayout.setVisibility(View.VISIBLE);

				final ImageView remotesImageView = (ImageView) findViewById(R.id.remotesImageView);
				remotesLinearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (zmote == 0) {
							zmote = 1;
							remotesImageView.setImageResource(resID1);
						} else {
							remotesImageView.setImageResource(resID2);
							zmote = 0;
						}
					}
				});
			}
			if (objStatusJSON.has("SECURITY") && objStatusJSON.getString("SECURITY").equals("yes")) {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		RelativeLayout okRelativeLayout = (RelativeLayout) findViewById(R.id.okRelativeLayout);
		okRelativeLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (setup_status == 0) {
					okTextView = (TextView) findViewById(R.id.okTextView);
					okTextView.setText("loding...");
					URLs.clear();

					if (voice == 1) {
						String voiceJSON = "voiceJSON.cfg";
						URLs.add(voiceJSON);
					}
					if (cctv == 1) {

					}
					if (zmote == 1) {

						String remotesJSON = "remotesJSON.cfg";
						URLs.add(remotesJSON);

						if (type.equals("pull")) {
							String zmoteJSON = "zmoteJSON.cfg";
							URLs.add(zmoteJSON);

							String remoteControlsJSON = "remoteControlsJSON.cfg";
							URLs.add(remoteControlsJSON);
						}

					}
					if (vavis == 1) {
						String masterJSON = "masterJSON.cfg";
						URLs.add(masterJSON);
					}
					if (scenes == 1) {
						String sceneJSON = "sceneJSON.cfg";
						URLs.add(sceneJSON);

					}
					if (type.equals("push")) {
						new writeURLs().execute();
					} else if (type.equals("pull")) {
						new readURLs().execute();
					}

				} else {
					if (type.equals("push")) {
						finish();
					} else {
						Intent i = new Intent(getBaseContext(), MainActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
					}
				}
			}
		});

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

	public String readFile(String FileName) {
		try {
			FileInputStream fis = openFileInput(FileName);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			return bufferedReader.readLine();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void loadCompleted(String status) {
		if (status.equals("success")) {
			if (type.equals("push")) {
				Toast.makeText(getApplicationContext(), "Config Push completed", Toast.LENGTH_SHORT).show();
				okTextView.setText("Exit");
				setup_status = 1;
			} else {
				Toast.makeText(getApplicationContext(), "Config fetch completed", Toast.LENGTH_SHORT).show();
				okTextView.setText("Finish & Launch");
				setup_status = 1;
			}

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
					if (CFG.equals("remoteControlsJSON.cfg")) {
						URL_NEW = "http://" + gatewayIP + "/readRemote/";
					} else if (CFG.equals("zmoteJSON.cfg")) {
						URL_NEW = "http://" + gatewayIP + "/readConfig/?filename=" + CFG;
					} else {
						URL_NEW = "http://" + gatewayIP + "/syncRead/?filename=" + CFG;
					}
					URL url = new URL(URL_NEW);
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setConnectTimeout(100);

					int responseCode = urlConnection.getResponseCode();
					if (responseCode == HttpURLConnection.HTTP_OK) {
						server_response = readStream(urlConnection.getInputStream());
						writeFile(CFG, server_response);
					} else {
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

	public class writeURLs extends AsyncTask<String, Void, String> {
		String server_response;
		String status = "success";

		@Override
		protected String doInBackground(String... strings) {

			Iterator<String> iter = URLs.iterator();
			try {
				while (iter.hasNext()) {
					@SuppressWarnings("unused")
					String result = null;
					BufferedReader inStream = null;
					String CFG = iter.next();

					String data = readFile(CFG);

					JSONObject jsonParam = new JSONObject();
					jsonParam.put("filename", CFG);
					jsonParam.put("data", data);

					HttpClient httpClient = new DefaultHttpClient();
					final EditText editTextView = (EditText) findViewById(R.id.editTextView);
					String gatewayIP = editTextView.getText().toString();
					HttpPost httpost = new HttpPost("http://" + gatewayIP + "/syncWrite/");
					StringEntity stringEntity = new StringEntity(jsonParam.toString(), HTTP.UTF_8);
					stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
					httpost.setEntity(stringEntity);

					HttpResponse response = httpClient.execute(httpost);
					inStream = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					StringBuffer buffer = new StringBuffer("");
					String line = "";
					String NL = System.getProperty("line.separator");
					while ((line = inStream.readLine()) != null) {
						buffer.append(line + NL);
					}
					inStream.close();
					result = buffer.toString();
				}
			} catch (MalformedURLException e) {
				status = "fail";
			} catch (IOException e) {
				status = "fail";
			} catch (JSONException e) {
				e.printStackTrace();
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
