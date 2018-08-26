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

public class SelectZmoteActivity extends BaseActivity {
	LinearLayout Masterlayout;
	LinearLayout DynamicSwitch;
	String type;
	String intent_zmote_id;
	String zmoteJSON = "zmoteJSON.cfg";
	String rawZmoteJSON = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		ArrayList<String> zmote_id = new ArrayList<String>();
		ArrayList<String> name = new ArrayList<String>();

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.blank);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		intent_zmote_id = intent.getStringExtra("zmote_id");

		Masterlayout = (LinearLayout) findViewById(R.id.Masterlayout);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ArrayList<View> custombuttonview = new ArrayList<View>();

		try {
			FileInputStream fis = openFileInput(zmoteJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawZmoteJSON;
			rawZmoteJSON = bufferedReader.readLine();

			JSONObject objJSON = new JSONObject(rawZmoteJSON);
			JSONArray arrJSON = objJSON.getJSONArray("ZMOTE");
			for (int i = 0; i < arrJSON.length(); i++) {
				zmote_id.add(Integer.toString(i));
				name.add(arrJSON.getJSONObject(i).getString("IP"));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < zmote_id.size(); i++) {
			custombuttonview.add(inflater.inflate(R.layout.remoteswitch, Masterlayout, false));
			Masterlayout.addView(custombuttonview.get(i));
		}

		int remoteLayout_ID = 0;
		int remoteTextView_ID = 0;

		for (int i = 0; i < zmote_id.size(); i++) {
			String cur_zmote_id = zmote_id.get(i);
			String cur_zmote_name = name.get(i);

			remoteLayout_ID = 1000 + i;
			remoteTextView_ID = 2000 + i;

			findViewById(R.id.remoteLayout).setId(remoteLayout_ID);
			findViewById(R.id.remoteTextView).setId(remoteTextView_ID);

			DynamicSwitch = (LinearLayout) findViewById(remoteLayout_ID);
			DynamicSwitch.setOnClickListener(getOnClickDoSomething(DynamicSwitch, cur_zmote_id));

			TextView remoteTextView = (TextView) findViewById(remoteTextView_ID);
			remoteTextView.setText(cur_zmote_name);

		}

	}

	View.OnClickListener getOnClickDoSomething(final LinearLayout buttonLayout, final String cur_zmote_id) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("zmote_id", cur_zmote_id);
				setResult(4, intent);
				finish();
			}
		};
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("zmote_id", intent_zmote_id);
		setResult(4, intent);
		finish();
	}

}