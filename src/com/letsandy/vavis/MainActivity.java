package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.letsandy.vavis.MessagingService;
import com.letsandy.vavis.MyPagerAdapter.FragmentLifecycle;
import com.letsandy.vavis.R;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends BaseFragmentActivity {

	private MyPagerAdapter pageAdapter;
	Uri imageURI;
	ImageView imageView;
	Intent startIntent;
	ViewPager pager;
	SharedPreferences mPrefs;
	Dialog dialog = null;
	int syncFlag = 1;
	private static final String FIRST_FLAG = "first_flag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);
		pageAdapter = new MyPagerAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.myViewPager);
		pager.setAdapter(pageAdapter);
		pager.setCurrentItem(1);
		pager.setOnPageChangeListener(pageChangeListener);

		SharedPreferences mPrefs1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Boolean configFetchSuccess = mPrefs1.getBoolean("setup_state", false);

		if (!configFetchSuccess) {
			Intent intent = new Intent(getApplicationContext(), SetupActivity.class);
			startActivity(intent);
		} else {

			String voiceJSON = "voiceJSON.cfg";
			String rawVoiceJSON = "{\"Voice\":[{\"VoiceString\":\"Hello World\",\"Command\":\"R01S01ON\"}]}";
			FileOutputStream outputStream;
			try {
				outputStream = openFileOutput(voiceJSON, Context.MODE_PRIVATE);
				outputStream.write(rawVoiceJSON.getBytes());
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				startIntent = new Intent(MainActivity.this, MessagingService.class);
				startService(startIntent);
			} catch (Exception e) {

			}
		}
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(FIRST_FLAG, 1);
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int newPosition) {
			FragmentLifecycle fragment = (FragmentLifecycle) pageAdapter.instantiateItem(pager, newPosition);
			if (fragment != null) {
				fragment.onResumeFragment();
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	boolean doubleBackToExitPressedOnce = false;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (pager.getCurrentItem() == 1) {
				onBackPressed();
			} else {
				pager.setCurrentItem(1, true);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(getApplicationContext(), "Click BACK again to exit", Toast.LENGTH_SHORT).show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}

	@SuppressLint("DefaultLocale")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 99 && resultCode == RESULT_OK) {
			ArrayList<String> google_voice_output = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			ArrayList<String> voice_commands = new ArrayList<String>();
			try {
				String voiceJSON = "voiceJSON.cfg";
				FileInputStream fis = openFileInput(voiceJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				String rawVoiceJSONCheck;
				rawVoiceJSONCheck = bufferedReader.readLine();

				JSONObject objJSON = new JSONObject(rawVoiceJSONCheck);
				JSONArray arrJSON = objJSON.getJSONArray("Voice");
				for (int i = 0; i < arrJSON.length(); i++) {
					voice_commands.add(arrJSON.getJSONObject(i).getString("VoiceString").toLowerCase());
				}
				google_voice_output.retainAll(voice_commands);
				String joined = TextUtils.join(", ", google_voice_output);
				for (int i = 0; i < arrJSON.length(); i++) {
					if (arrJSON.getJSONObject(i).getString("VoiceString").toLowerCase().equals(joined)) {
						String COMMAND = arrJSON.getJSONObject(i).getString("Command");
						String final_command = null;
						try {
							JSONObject tmpJSON = new JSONObject();
							tmpJSON.put("Request", COMMAND);
							tmpJSON.put("RequestType", "Normal");
							final_command = tmpJSON.toString();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						messagePublish("request/switchControl", final_command);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void messagePublish(String TOPIC, String MESSAGE) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("com.letsandy.publishMQTT");
		broadcastIntent.putExtra("TOPIC", TOPIC);
		broadcastIntent.putExtra("MESSAGE", MESSAGE);
		sendBroadcast(broadcastIntent);
	}
}
