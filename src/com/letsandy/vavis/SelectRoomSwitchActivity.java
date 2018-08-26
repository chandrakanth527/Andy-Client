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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectRoomSwitchActivity extends BaseActivity {
	LinearLayout Masterlayout;
	LinearLayout DynamicSwitch;
	String type;
	String intent_room_id;
	String intent_switch_id;
	String masterJSON = "masterJSON.cfg";
	String rawMasterJSON = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		ArrayList<String> roomswitch_id = new ArrayList<String>();
		ArrayList<String> roomswitch_name = new ArrayList<String>();
		ArrayList<String> roomswitch_icon = new ArrayList<String>();
		ArrayList<String> roomswitch_type = new ArrayList<String>();

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.blank);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		intent_room_id = intent.getStringExtra("room_id");
		intent_switch_id = intent.getStringExtra("switch_id");

		Masterlayout = (LinearLayout) findViewById(R.id.Masterlayout);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ArrayList<View> custombuttonview = new ArrayList<View>();

		if (type.equals("room")) {
			try {
				FileInputStream fis = openFileInput(masterJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				String rawMasterJSON;
				rawMasterJSON = bufferedReader.readLine();

				JSONObject objJSON = new JSONObject(rawMasterJSON);
				JSONArray arrJSON = objJSON.getJSONArray("Room");
				for (int i = 0; i < arrJSON.length(); i++) {

					roomswitch_id.add(Integer.toString(i));
					roomswitch_name.add(arrJSON.getJSONObject(i).getString("RoomName"));
					roomswitch_icon.add(arrJSON.getJSONObject(i).getString("RoomIcon"));

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (type.equals("switch")) {
			try {
				FileInputStream fis = openFileInput(masterJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				String rawMasterJSON;
				rawMasterJSON = bufferedReader.readLine();

				JSONObject objJSON = new JSONObject(rawMasterJSON);
				JSONArray arrJSON = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(intent_room_id)).getJSONArray("Switch");
				for (int i = 0; i < arrJSON.length(); i++) {
					roomswitch_id.add(Integer.toString(i));
					roomswitch_name.add(arrJSON.getJSONObject(i).getString("SwitchName"));
					roomswitch_icon.add(arrJSON.getJSONObject(i).getString("SwitchIcon"));
					roomswitch_type.add(arrJSON.getJSONObject(i).getString("Type"));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		for (int i = 0; i < roomswitch_id.size(); i++) {
			custombuttonview.add(inflater.inflate(R.layout.roomswitch, Masterlayout, false));
			Masterlayout.addView(custombuttonview.get(i));
		}

		int roomswitchLayout_ID = 0;
		int roomswitchTextView_ID = 0;
		int roomswitchImageView_ID = 0;
		int roomswitchImageViewBackground_ID = 0;
		for (int i = 0; i < roomswitch_id.size(); i++) {
			String cur_roomswitch_id = roomswitch_id.get(i);
			String cur_roomswitch_name = roomswitch_name.get(i);
			String cur_roomswitch_icon = roomswitch_icon.get(i);
			roomswitchLayout_ID = 1000 + i;
			roomswitchTextView_ID = 2000 + i;
			roomswitchImageView_ID = 3000 + i;
			roomswitchImageViewBackground_ID = 4000 + i;
			findViewById(R.id.roomswitchLayout).setId(roomswitchLayout_ID);
			findViewById(R.id.roomswitchTextView).setId(roomswitchTextView_ID);
			findViewById(R.id.roomswitchImageView).setId(roomswitchImageView_ID);
			findViewById(R.id.roomswitchImageViewBackground).setId(roomswitchImageViewBackground_ID);
			DynamicSwitch = (LinearLayout) findViewById(roomswitchLayout_ID);
			DynamicSwitch.setOnClickListener(getOnClickDoSomething(DynamicSwitch, cur_roomswitch_id));
			TextView roomswitchTextView = (TextView) findViewById(roomswitchTextView_ID);
			roomswitchTextView.setText(cur_roomswitch_name);
			int resID = getResources().getIdentifier(cur_roomswitch_icon, "drawable", this.getPackageName());
			ImageView room_image = (ImageView) findViewById(roomswitchImageView_ID);
			room_image.setImageResource(resID);

			if (type.equals("switch")) {
				if (roomswitch_type.get(i).equals("Scene")) {
					findViewById(roomswitchLayout_ID).setVisibility(View.GONE);
				}
			}
		}

	}

	View.OnClickListener getOnClickDoSomething(final LinearLayout buttonLayout, final String cur_roomswitch_id) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("type", type);
				intent.putExtra("roomswitch_id", cur_roomswitch_id);
				setResult(2, intent);
				finish();
			}
		};
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		if (type.equals("room")) {
			intent.putExtra("type", "room");
			intent.putExtra("roomswitch_id", intent_room_id);
		} else if (type.equals("switch")) {
			intent.putExtra("type", "switch");
			intent.putExtra("roomswitch_id", intent_switch_id);
		}
		setResult(2, intent);
		finish();
	}

}