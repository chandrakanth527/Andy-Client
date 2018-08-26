package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditRemoteActivity extends BaseActivity {
	LinearLayout Masterlayout;
	LinearLayout DynamicSwitch;
	RelativeLayout addRelativeLayout;
	String remotesJSON = "remotesJSON.cfg";
	String masterJSON = "masterJSON.cfg";
	String rawRemotesJSON;
	String rawMasterJSON = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.remoteblank);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);
	}

	protected void onResume() {
		super.onResume();
		updateView();
	}

	public void updateView() {
		ArrayList<String> remoteName = new ArrayList<String>();
		ArrayList<String> command = new ArrayList<String>();
		Masterlayout = (LinearLayout) findViewById(R.id.Masterlayout);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ArrayList<View> custombuttonview = new ArrayList<View>();
		Masterlayout.removeAllViews();
		try {
			FileInputStream fis = openFileInput(remotesJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);

			rawRemotesJSON = bufferedReader.readLine();

			JSONObject objJSON = new JSONObject(rawRemotesJSON);
			JSONArray arrJSON = objJSON.getJSONArray("REMOTE");
			for (int i = 0; i < arrJSON.length(); i++) {

				remoteName.add(arrJSON.getJSONObject(i).getString("NAME"));
				command.add(arrJSON.getJSONObject(i).getString("CODE"));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FileInputStream fis = openFileInput(masterJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			rawMasterJSON = bufferedReader.readLine();

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < remoteName.size(); i++) {
			custombuttonview.add(inflater.inflate(R.layout.remoteswitch, Masterlayout, false));
			Masterlayout.addView(custombuttonview.get(i));
		}

		int remoteLayout_ID = 0;
		int remoteTextView_ID = 0;
		for (int i = 0; i < remoteName.size(); i++) {
			String cur_remoteName = remoteName.get(i);
			String cur_command = command.get(i);
			String cur_room = cur_command.substring(4, 6);

			String cur_roomname = null;

			JSONObject objJSON;
			try {
				objJSON = new JSONObject(rawMasterJSON);
				cur_roomname = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getString("RoomName");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			remoteLayout_ID = 1000 + i;
			remoteTextView_ID = 2000 + i;

			findViewById(R.id.remoteLayout).setId(remoteLayout_ID);
			findViewById(R.id.remoteTextView).setId(remoteTextView_ID);

			DynamicSwitch = (LinearLayout) findViewById(remoteLayout_ID);
			DynamicSwitch.setOnClickListener(getOnClickDoSomething(DynamicSwitch, i));

			TextView remoteTextView = (TextView) findViewById(remoteTextView_ID);
			remoteTextView.setText(cur_roomname + " - " + cur_remoteName);

		}
		addRelativeLayout = (RelativeLayout) findViewById(R.id.addRelativeLayout);
		addRelativeLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), EditAddRemoteActivity.class);
				intent.putExtra("Type", "New");
				intent.putExtra("remotePos", "1");
				startActivity(intent);

			}

		});

	}

	View.OnClickListener getOnClickDoSomething(final LinearLayout buttonLayout, final int remotePos) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), EditAddRemoteActivity.class);
				intent.putExtra("Type", "Old");
				intent.putExtra("remotePos", remotePos);
				startActivity(intent);
			}
		};

	}

}