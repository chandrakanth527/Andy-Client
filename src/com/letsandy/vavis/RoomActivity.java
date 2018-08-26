package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.*;
import com.letsandy.vavis.SwipeDetector.Action;

@SuppressLint("NewApi")
public class RoomActivity extends BaseActivity {
	LinearLayout Masterlayout;
	LinearLayout DynamicSwitch;
	String masterJSON = "masterJSON.cfg";
	String switchStatusJSON = "statusJSON.cfg";
	updateSwitchView updateSwitchView;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ArrayList<String> room_id = new ArrayList<String>();
		ArrayList<String> room_name = new ArrayList<String>();
		ArrayList<String> room_icon = new ArrayList<String>();

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.roomblank);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);

		updateSwitchView = new updateSwitchView();
		IntentFilter intentUpdateSwitchView = new IntentFilter("com.letsandy.updateSwitchView");
		registerReceiver(updateSwitchView, intentUpdateSwitchView);

		Masterlayout = (LinearLayout) findViewById(R.id.Masterlayout);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		try {
			FileInputStream fis = openFileInput(masterJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawMasterJSON;
			rawMasterJSON = bufferedReader.readLine();

			JSONObject objJSON = new JSONObject(rawMasterJSON);
			JSONArray arrJSON = objJSON.getJSONArray("Room");
			for (int i = 0; i < arrJSON.length(); i++) {

				room_id.add(Integer.toString(i));
				room_name.add(arrJSON.getJSONObject(i).getString("RoomName"));
				room_icon.add(arrJSON.getJSONObject(i).getString("RoomIcon"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject objStatusJSON = null;
		try {
			FileInputStream fis = openFileInput(switchStatusJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawSwitchStatusJSON;
			rawSwitchStatusJSON = bufferedReader.readLine();
			objStatusJSON = new JSONObject(rawSwitchStatusJSON);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<View> custombuttonview = new ArrayList<View>();

		int LOOP = room_id.size();
		for (int cur_loop = 0; cur_loop < LOOP; cur_loop++) {
			custombuttonview.add(inflater.inflate(R.layout.roombutton, Masterlayout, false));
			Masterlayout.addView(custombuttonview.get(cur_loop));
		}

		int roomLayout_ID = 0;
		int roomTextView_ID = 0;
		int roomStatus_ID = 0;
		for (int i = 0; i < room_id.size(); i++) {
			String cur_room_id = room_id.get(i);
			String cur_room_name = room_name.get(i);
			roomLayout_ID = 1000 + i;
			roomTextView_ID = 2000 + i;
			roomStatus_ID = 3000 + i;
			findViewById(R.id.roomLayout).setId(roomLayout_ID);
			findViewById(R.id.roomTextView).setId(roomTextView_ID);
			findViewById(R.id.roomStatus).setId(roomStatus_ID);
			DynamicSwitch = (LinearLayout) findViewById(roomLayout_ID);
			SwipeDetector swipeDetector;
			swipeDetector = new SwipeDetector();
			DynamicSwitch.setOnTouchListener(swipeDetector);
			DynamicSwitch.setOnClickListener(getOnClickDoSomething(DynamicSwitch, cur_room_id, swipeDetector));
			TextView roomTextView = (TextView) findViewById(roomTextView_ID);
			roomTextView.setText(cur_room_name);
			findViewById(roomStatus_ID).setVisibility(View.INVISIBLE);

			JSONArray arrStatusJSON;
			try {
				arrStatusJSON = objStatusJSON.getJSONArray("Room").getJSONObject(i).getJSONArray("Switch");
				for (int j = 0; j < arrStatusJSON.length(); j++) {
					if (arrStatusJSON.getJSONObject(j).getString("SwitchStatus").equals("ON")) {
						findViewById(roomStatus_ID).setVisibility(View.VISIBLE);
						break;
					}
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		}
	}

	public void resetview() {
		JSONObject objStatusJSON = null;
		int room_size = 0;
		try {
			FileInputStream fis = openFileInput(switchStatusJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawSwitchStatusJSON;
			rawSwitchStatusJSON = bufferedReader.readLine();
			objStatusJSON = new JSONObject(rawSwitchStatusJSON);
			room_size = objStatusJSON.getJSONArray("Room").length();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int roomStatus_ID = 0;
		for (int i = 0; i < room_size; i++) {
			roomStatus_ID = 3000 + i;
			findViewById(roomStatus_ID).setVisibility(View.INVISIBLE);
			JSONArray arrStatusJSON;
			try {
				arrStatusJSON = objStatusJSON.getJSONArray("Room").getJSONObject(i).getJSONArray("Switch");
				for (int j = 0; j < arrStatusJSON.length(); j++) {
					if (arrStatusJSON.getJSONObject(j).getString("SwitchStatus").equals("ON")) {
						findViewById(roomStatus_ID).setVisibility(View.VISIBLE);
						break;
					}
				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

		}
	}

	View.OnClickListener getOnClickDoSomething(final LinearLayout buttonLayout, final String room_id, final SwipeDetector swipeDetector) {
		return new View.OnClickListener() {
			@Override
			@SuppressLint("NewApi")
			public void onClick(View v) {

				if (swipeDetector.swipeDetected()) {
					if (swipeDetector.getAction() == Action.RL) {
						int roomLayout_ID = 1000 + Integer.parseInt(room_id);
						ObjectAnimator colorFade = ObjectAnimator.ofObject(findViewById(roomLayout_ID), "backgroundColor", new ArgbEvaluator(), 0xff00a6d6, 0xff191919);
						colorFade.setDuration(700);
						colorFade.start();
						messagePublish("request/switchAllOff", "R" + String.format("%02d", Integer.parseInt(room_id)) + "OF");
					} else if (swipeDetector.getAction() == Action.LR) {
					} else if (swipeDetector.getAction() == Action.TB) {
					} else if (swipeDetector.getAction() == Action.BT) {
					}
				} else {
					Intent intent = new Intent(getApplicationContext(), SwitchActivity.class);
					intent.putExtra("room_id", room_id);
					intent.putExtra("type", "All");
					startActivity(intent);
				}
			}

		};

	}

	public void messagePublish(String TOPIC, String MESSAGE) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("com.letsandy.publishMQTT");
		broadcastIntent.putExtra("TOPIC", TOPIC);
		broadcastIntent.putExtra("MESSAGE", MESSAGE);
		sendBroadcast(broadcastIntent);
	}

	public class updateSwitchView extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			resetview();
		}

	}

}
