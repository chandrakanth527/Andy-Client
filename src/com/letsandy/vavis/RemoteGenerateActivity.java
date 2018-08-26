package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class RemoteGenerateActivity extends BaseFragmentActivity {
	MyPageAdapter pageAdapter;
	publishHTTP publishHTTP;
	String Code = "";
	int ZMOTE;
	int ROOM;
	int REMOTE;
	String Type = "";
	String IP = "";
	String UID = "";
	JSONArray keyJSON;
	int temp = 18;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.remote_page_view);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Code = intent.getStringExtra("Code");

		ZMOTE = Integer.parseInt(Code.substring(1, 3));
		ROOM = Integer.parseInt(Code.substring(4, 6));
		REMOTE = Integer.parseInt(Code.substring(7, 9));

		String zmoteJSON = "zmoteJSON.cfg";
		String rawZmoteJSON = "";

		try {
			FileInputStream fis = openFileInput(zmoteJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			rawZmoteJSON = bufferedReader.readLine();
			JSONObject objJSON = new JSONObject(rawZmoteJSON);
			JSONArray arrJSON = objJSON.getJSONArray("ZMOTE");

			IP = arrJSON.getJSONObject(ZMOTE).getString("IP");
			UID = arrJSON.getJSONObject(ZMOTE).getString("UID");

		} catch (Exception e) {
			e.printStackTrace();
		}

		String remoteControlsJSON = "remoteControlsJSON.cfg";
		String rawRemoteControlsJSON = "";
		try {
			FileInputStream fis = openFileInput(remoteControlsJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			rawRemoteControlsJSON = bufferedReader.readLine();
			JSONObject objJSON = new JSONObject(rawRemoteControlsJSON);
			JSONArray arrJSON = objJSON.getJSONArray("REMOTECONTROLS");
			Type = arrJSON.getJSONObject(REMOTE).getString("type");
			keyJSON = arrJSON.getJSONObject(REMOTE).getJSONArray("keys");

		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Fragment> fragments = getFragments();
		pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
		ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setAdapter(pageAdapter);

		publishHTTP = new publishHTTP();
		IntentFilter intentPublishHTTP = new IntentFilter("com.letsandy.publishHTTP");
		registerReceiver(publishHTTP, intentPublishHTTP);

	}

	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(publishHTTP);
	}

	private List<Fragment> getFragments() {
		List<Fragment> fList = new ArrayList<Fragment>();
		if (Type.equals("TV")) {
			fList.add(RemoteFragment2.newInstance("Fragment 2"));
			fList.add(RemoteFragment1.newInstance("Fragment 1"));
		} else if (Type.equals("CABLE-STB")) {
			fList.add(RemoteFragment2.newInstance("Fragment 2"));
			fList.add(RemoteFragment1.newInstance("Fragment 1"));
		} else if (Type.equals("DVD-BD")) {
			fList.add(RemoteFragment3.newInstance("Fragment 3"));
			fList.add(RemoteFragment1.newInstance("Fragment 1"));
		} else if (Type.equals("PRJCTR")) {
			fList.add(RemoteFragment4.newInstance("Fragment 4"));
		} else if (Type.equals("AC")) {
			fList.add(RemoteFragment5.newInstance("Fragment 5"));
		}
		return fList;
	}

	private class MyPageAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments;

		public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int position) {
			return this.fragments.get(position);
		}

		@Override
		public int getCount() {
			return this.fragments.size();
		}
	}

	public class publishHTTP extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
		}

	}

	public void buttonOnClick(View view) throws JSONException {
		String KEY = getResources().getResourceEntryName(view.getId());
		if (Type.equals("AC")) {
			if (KEY.equals("KEY_UP")) {
				if (temp != 30) {
					temp++;
				}
				TextView temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
				temperatureTextView.setText(Integer.toString(temp));
				KEY = "KEY_" + temp;
			} else if (KEY.equals("KEY_DOWN")) {
				if (temp != 16) {
					temp--;
				}
				TextView temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
				temperatureTextView.setText(Integer.toString(temp));
				KEY = "KEY_" + temp;
			}
		}

		for (int i = 0; i < keyJSON.length(); i++) {
			String KEY_JSON = keyJSON.getJSONObject(i).getString("key");
			if (KEY_JSON.equals(KEY)) {
				String CODE = keyJSON.getJSONObject(i).getString("code");
				if (!CODE.isEmpty()) {
					CODE = "sendir,1:1,0," + CODE;
					String url = "http://" + IP + "/v2/" + UID;
					new httpRequest().execute(url, CODE);
				}
				break;
			}
		}
	}

	private class httpRequest extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... args) {
			String result = null;
			BufferedReader inStream = null;
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpost = new HttpPost(args[0]);
				StringEntity stringEntity = new StringEntity(args[1], HTTP.UTF_8);
				stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "text/plain"));
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
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				if (inStream != null) {
					try {
						inStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return result;
		}

		protected void onPostExecute(String result) {
		}

	}
}
