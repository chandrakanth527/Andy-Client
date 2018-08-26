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

public class VoiceActivity extends BaseActivity {
	LinearLayout Masterlayout;
	LinearLayout DynamicSwitch;
	RelativeLayout addRelativeLayout;
	String voiceJSON = "voiceJSON.cfg";
	String masterJSON = "masterJSON.cfg";
	String rawVoiceJSON;
	String rawMasterJSON = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.voiceblank);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);
	}

	protected void onResume() {
		super.onResume();
		updateView();
	}

	public void updateView() {
		ArrayList<String> voiceString = new ArrayList<String>();
		ArrayList<String> command = new ArrayList<String>();
		Masterlayout = (LinearLayout) findViewById(R.id.Masterlayout);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ArrayList<View> custombuttonview = new ArrayList<View>();
		Masterlayout.removeAllViews();
		try {
			FileInputStream fis = openFileInput(voiceJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);

			rawVoiceJSON = bufferedReader.readLine();

			JSONObject objJSON = new JSONObject(rawVoiceJSON);
			JSONArray arrJSON = objJSON.getJSONArray("Voice");
			for (int i = 0; i < arrJSON.length(); i++) {

				voiceString.add(arrJSON.getJSONObject(i).getString("VoiceString"));
				command.add(arrJSON.getJSONObject(i).getString("Command"));

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

		for (int i = 0; i < voiceString.size(); i++) {
			custombuttonview.add(inflater.inflate(R.layout.voiceswitch, Masterlayout, false));
			Masterlayout.addView(custombuttonview.get(i));
		}

		int voiceLayout_ID = 0;
		int commandTextView_ID = 0;
		int roomswitchTextView_ID = 0;
		for (int i = 0; i < voiceString.size(); i++) {
			String cur_command = voiceString.get(i);
			String cur_roomswitch_info = command.get(i);
			String cur_room = cur_roomswitch_info.substring(1, 3);
			String cur_switch = cur_roomswitch_info.substring(4, 6);
			String cur_switch_status = cur_roomswitch_info.substring(6, 8);
			String cur_roomname = null;
			String cur_switchname = null;
			JSONObject objJSON;
			try {
				objJSON = new JSONObject(rawMasterJSON);
				cur_roomname = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getString("RoomName");
				cur_switchname = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getJSONArray("Switch").getJSONObject(Integer.parseInt(cur_switch)).getString("SwitchName");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			voiceLayout_ID = 1000 + i;
			commandTextView_ID = 2000 + i;
			roomswitchTextView_ID = 4000 + i;

			findViewById(R.id.voiceLayout).setId(voiceLayout_ID);
			findViewById(R.id.commandTextView).setId(commandTextView_ID);
			findViewById(R.id.roomswitchTextView).setId(roomswitchTextView_ID);

			String cur_switch_format_status = null;
			if (cur_switch_status.equals("ON")) {
				cur_switch_format_status = "On";

			} else if (cur_switch_status.equals("OF")) {
				cur_switch_format_status = "Off";
			} else {
				cur_switch_format_status = cur_switch_status;
			}

			DynamicSwitch = (LinearLayout) findViewById(voiceLayout_ID);
			DynamicSwitch.setOnClickListener(getOnClickDoSomething(DynamicSwitch, i));
			TextView commandTextView = (TextView) findViewById(commandTextView_ID);
			commandTextView.setText(cur_command);
			TextView roomswitchTextView = (TextView) findViewById(roomswitchTextView_ID);
			roomswitchTextView.setText(cur_roomname + " - " + cur_switchname + " - " + cur_switch_format_status);

		}
		addRelativeLayout = (RelativeLayout) findViewById(R.id.addRelativeLayout);
		addRelativeLayout.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), AddVoiceActivity.class);
				intent.putExtra("Type", "New");
				intent.putExtra("voiceCommandPos", "1");
				startActivity(intent);

			}

		});

	}

	View.OnClickListener getOnClickDoSomething(final LinearLayout buttonLayout, final int voiceCommandPos) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), AddVoiceActivity.class);
				intent.putExtra("Type", "Old");
				intent.putExtra("voiceCommandPos", voiceCommandPos);
				startActivity(intent);
			}
		};

	}

}