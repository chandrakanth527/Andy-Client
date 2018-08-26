package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectRemoteActivity extends BaseActivity {
	LinearLayout Masterlayout;
	LinearLayout DynamicSwitch;
	String type;
	String intent_remote_id;
	String remoteControlsJSON = "remoteControlsJSON.cfg";
	String rawRemoteControlsJSON = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		ArrayList<String> remote_id = new ArrayList<String>();
		ArrayList<String> brand = new ArrayList<String>();
		ArrayList<String> type = new ArrayList<String>();

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.blank);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		intent_remote_id = intent.getStringExtra("remote_id");

		Masterlayout = (LinearLayout) findViewById(R.id.Masterlayout);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ArrayList<View> custombuttonview = new ArrayList<View>();

		try {
			FileInputStream fis = openFileInput(remoteControlsJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawRemoteControlsJSON;
			rawRemoteControlsJSON = bufferedReader.readLine();

			JSONObject objJSON = new JSONObject(rawRemoteControlsJSON);
			JSONArray arrJSON = objJSON.getJSONArray("REMOTECONTROLS");
			for (int i = 0; i < arrJSON.length(); i++) {
				remote_id.add(Integer.toString(i));
				brand.add(arrJSON.getJSONObject(i).getString("brand"));
				type.add(arrJSON.getJSONObject(i).getString("type"));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < remote_id.size(); i++) {
			custombuttonview.add(inflater.inflate(R.layout.remoteswitch, Masterlayout, false));
			Masterlayout.addView(custombuttonview.get(i));
		}

		int remoteLayout_ID = 0;
		int remoteTextView_ID = 0;

		for (int i = 0; i < remote_id.size(); i++) {
			String cur_remote_id = remote_id.get(i);
			String cur_remote_name = brand.get(i) + " - " + type.get(i);

			remoteLayout_ID = 1000 + i;
			remoteTextView_ID = 2000 + i;

			findViewById(R.id.remoteLayout).setId(remoteLayout_ID);
			findViewById(R.id.remoteTextView).setId(remoteTextView_ID);

			DynamicSwitch = (LinearLayout) findViewById(remoteLayout_ID);
			DynamicSwitch.setOnClickListener(getOnClickDoSomething(DynamicSwitch, cur_remote_id));

			TextView remoteTextView = (TextView) findViewById(remoteTextView_ID);
			remoteTextView.setText(cur_remote_name);

		}

	}

	View.OnClickListener getOnClickDoSomething(final LinearLayout buttonLayout, final String cur_remote_id) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("remote_id", cur_remote_id);
				setResult(3, intent);
				finish();
			}
		};
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("remote_id", intent_remote_id);
		setResult(3, intent);
		finish();
	}

}