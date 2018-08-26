package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class AddEventActivity extends BaseActivity implements OnSeekBarChangeListener {
	LinearLayout Masterlayout;
	LinearLayout DynamicSwitch;
	TextView roomTextView;
	TextView switchTextView;
	TextView updateTextView;
	RelativeLayout roomLayout;
	RelativeLayout switchLayout;
	RelativeLayout onRelativeLayout;
	RelativeLayout offRelativeLayout;
	RelativeLayout deleteRelativeLayout;
	RelativeLayout updateRelativeLayout;
	RelativeLayout dimmerRelativeLayout;
	SeekBar dimmerSeekBar;
	ImageView onImageView;
	ImageView offImageView;
	String cur_room;
	String cur_switch;
	String status;
	String masterJSON = "masterJSON.cfg";
	String rawMasterJSON = null;
	String cur_switch_status;
	String cur_roomname;
	String cur_switchname;
	String cur_switchtype;
	int curProgress;
	String Type = null;
	int scene_id;
	String command;
	int command_id;
	ArrayList<String> room_raw;
	ArrayList<String> switch_raw;
	int resID1;
	int resID2;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.setevent);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Type = intent.getStringExtra("Type");
		scene_id = intent.getIntExtra("SceneId", 0);
		command_id = intent.getIntExtra("command_id", 0);

		room_raw = new ArrayList<String>();
		switch_raw = new ArrayList<String>();
		dimmerRelativeLayout = (RelativeLayout) findViewById(R.id.dimmerRelativeLayout);
		dimmerSeekBar = (SeekBar) findViewById(R.id.dimmerSeekBar);
		dimmerSeekBar.setOnSeekBarChangeListener(this);
		if (Type.equals("New")) {
			updateTextView = (TextView) findViewById(R.id.updateTextView);
			updateTextView.setText("Add Event");
			cur_room = "xx";
			cur_switch = "xx";
			cur_switchtype = "Normal";
			cur_switch_status = "ON";
			cur_roomname = "Select Room";
			cur_switchname = "Select Switch";

			try {
				FileInputStream fis = openFileInput(masterJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				rawMasterJSON = bufferedReader.readLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			updateTextView = (TextView) findViewById(R.id.updateTextView);
			updateTextView.setText("Update");

			String sceneJSON = "sceneJSON.cfg";
			try {
				FileInputStream fis = openFileInput(sceneJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				String rawSceneMasterJSON;
				rawSceneMasterJSON = bufferedReader.readLine();
				JSONObject objJSON = new JSONObject(rawSceneMasterJSON);
				JSONArray arrJSON = objJSON.getJSONArray("Scene");
				JSONArray arrJSONCommands = arrJSON.getJSONObject(scene_id).getJSONArray("Commands");
				command = arrJSONCommands.getJSONObject(command_id).getString("Command");
			} catch (Exception e) {
				e.printStackTrace();
			}
			cur_room = command.substring(1, 3);
			cur_switch = command.substring(4, 6);
			// cur_switch_status = command.substring(6, 8);
			if (command.substring(6, 8).equals("OF")) {
				cur_switch_status = "OF";

			} else if (command.substring(6, 8).equals("ON")) {
				cur_switch_status = "ON";
			} else {
				cur_switch_status = "ON";
				curProgress = Integer.parseInt(command.substring(6, 8));
			}
			cur_switchtype = "Normal";
			try {
				FileInputStream fis = openFileInput(masterJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				rawMasterJSON = bufferedReader.readLine();
				JSONObject objJSON;
				objJSON = new JSONObject(rawMasterJSON);
				cur_roomname = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getString("RoomName");
				cur_switchname = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getJSONArray("Switch").getJSONObject(Integer.parseInt(cur_switch)).getString("SwitchName");
				cur_switchtype = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getJSONArray("Switch").getJSONObject(Integer.parseInt(cur_switch)).getString("Type");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		roomTextView = (TextView) findViewById(R.id.roomTextView);
		roomTextView.setText(cur_roomname);
		switchTextView = (TextView) findViewById(R.id.switchTextView);
		switchTextView.setText(cur_switchname);

		if ((cur_switchtype.equals("Dimmer") || cur_switchtype.equals("Fan")) && cur_switch_status.equals("ON")) {
			dimmerRelativeLayout.setVisibility(RelativeLayout.VISIBLE);
			dimmerSeekBar.setProgress(curProgress);
		} else {
			dimmerRelativeLayout.setVisibility(RelativeLayout.GONE);
		}
		roomLayout = (RelativeLayout) findViewById(R.id.roomLayout);
		roomLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SelectRoomSwitchActivity.class);
				intent.putExtra("type", "room");
				intent.putExtra("room_id", cur_room);
				intent.putExtra("switch_id", cur_switch);
				startActivityForResult(intent, 2);
			}
		});

		switchLayout = (RelativeLayout) findViewById(R.id.switchLayout);
		switchLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (cur_room.equals("xx")) {
					Toast.makeText(getApplicationContext(), "Select Room", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(getApplicationContext(), SelectRoomSwitchActivity.class);
					intent.putExtra("type", "switch");
					intent.putExtra("room_id", cur_room);
					intent.putExtra("switch_id", cur_switch);
					startActivityForResult(intent, 2);
				}
			}
		});

		resID1 = getResources().getIdentifier("ic_radio1", "drawable", this.getPackageName());
		resID2 = getResources().getIdentifier("ic_radio2", "drawable", this.getPackageName());
		if (cur_switch_status.equals("ON")) {
			onImageView = (ImageView) findViewById(R.id.onImageView);
			onImageView.setImageResource(resID1);
			offImageView = (ImageView) findViewById(R.id.offImageView);
			offImageView.setImageResource(resID2);

		} else {
			onImageView = (ImageView) findViewById(R.id.onImageView);
			onImageView.setImageResource(resID2);
			offImageView = (ImageView) findViewById(R.id.offImageView);
			offImageView.setImageResource(resID1);

		}

		onRelativeLayout = (RelativeLayout) findViewById(R.id.onRelativeLayout);
		onRelativeLayout.setClickable(true);
		onRelativeLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cur_switch_status = "ON";
				onImageView.setImageResource(resID1);
				offImageView.setImageResource(resID2);
				if (cur_switchtype.equals("Dimmer") || cur_switchtype.equals("Fan")) {
					dimmerRelativeLayout.setVisibility(RelativeLayout.VISIBLE);
				}
			}
		});

		offRelativeLayout = (RelativeLayout) findViewById(R.id.offRelativeLayout);
		offRelativeLayout.setClickable(true);
		offRelativeLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cur_switch_status = "OF";
				onImageView.setImageResource(resID2);
				offImageView.setImageResource(resID1);
				dimmerRelativeLayout.setVisibility(RelativeLayout.GONE);
			}
		});

		deleteRelativeLayout = (RelativeLayout) findViewById(R.id.deleteRelativeLayout);
		deleteRelativeLayout.setClickable(true);
		deleteRelativeLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (!Type.equals("New")) {
					String sceneJSON = "sceneJSON.cfg";
					try {
						FileInputStream fis = openFileInput(sceneJSON);
						InputStreamReader isr = new InputStreamReader(fis);
						BufferedReader bufferedReader = new BufferedReader(isr);
						String rawSceneMasterJSON;
						rawSceneMasterJSON = bufferedReader.readLine();

						JSONObject objJSON = new JSONObject(rawSceneMasterJSON);
						JSONArray arrJSON = objJSON.getJSONArray("Scene");
						JSONArray arrJSONCommands = arrJSON.getJSONObject(scene_id).getJSONArray("Commands");
						arrJSONCommands.remove(command_id);
						rawSceneMasterJSON = objJSON.toString();
						FileOutputStream outputStream = openFileOutput(sceneJSON, Context.MODE_PRIVATE);
						outputStream.write(rawSceneMasterJSON.getBytes());
						outputStream.close();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				finish();

			}
		});

		updateRelativeLayout = (RelativeLayout) findViewById(R.id.updateRelativeLayout);
		updateRelativeLayout.setClickable(true);
		updateRelativeLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (cur_switchname.equals("null")) {
					Toast.makeText(getApplicationContext(), "Select Switch", Toast.LENGTH_SHORT).show();
				} else if (cur_room.equals("xx")) {
					Toast.makeText(getApplicationContext(), "Select Room", Toast.LENGTH_SHORT).show();
				} else if (cur_switch.equals("xx")) {
					Toast.makeText(getApplicationContext(), "Select Switch", Toast.LENGTH_SHORT).show();
				} else {
					String temp_status = null;
					if (cur_switch_status.equals("ON") && (cur_switchtype.equals("Dimmer")||cur_switchtype.equals("Fan"))) {
						temp_status = String.format("%02d", curProgress);
					} else {
						temp_status = cur_switch_status;
					}
					String command_new = "R" + String.format("%02d", Integer.parseInt(cur_room)) + "S" + String.format("%02d", Integer.parseInt(cur_switch)) + temp_status;
					String sceneJSON = "sceneJSON.cfg";
					try {
						FileInputStream fis = openFileInput(sceneJSON);
						InputStreamReader isr = new InputStreamReader(fis);
						BufferedReader bufferedReader = new BufferedReader(isr);
						String rawSceneMasterJSON;
						rawSceneMasterJSON = bufferedReader.readLine();
						JSONObject objJSON = new JSONObject(rawSceneMasterJSON);
						JSONArray arrJSON = objJSON.getJSONArray("Scene").getJSONObject(scene_id).getJSONArray("Commands");
						if (Type.equals("New")) {
							JSONObject newJSON = new JSONObject();
							newJSON.put("Type", "Switch");
							newJSON.put("Command", command_new);
							arrJSON.put(newJSON);
						} else {
							arrJSON.getJSONObject(command_id).remove("Command");
							arrJSON.getJSONObject(command_id).put("Command", command_new);
						}
						rawSceneMasterJSON = objJSON.toString();
						FileOutputStream outputStream = openFileOutput(sceneJSON, Context.MODE_PRIVATE);
						outputStream.write(rawSceneMasterJSON.getBytes());
						outputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

					finish();
				}
			}
		});

	}

	public void onProgressChanged(SeekBar dimmerSeekBar, int progress, boolean fromUser) {

		TextView dimmerIntensity = (TextView) findViewById(R.id.dimmerIntensity);
		dimmerIntensity.setText(Integer.toString(progress));
		curProgress = progress;
	}

	public void onStartTrackingTouch(SeekBar dimmerSeekBar) {
	}

	@SuppressLint("DefaultLocale")
	public void onStopTrackingTouch(SeekBar dimmerSeekBar) {
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 2) {
			if (data.getStringExtra("type").equals("room")) {
				String return_room_id = data.getStringExtra("roomswitch_id");
				if (!return_room_id.equals(cur_room)) {
					cur_room = return_room_id;
					JSONObject objJSON;
					try {
						objJSON = new JSONObject(rawMasterJSON);
						cur_roomname = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getString("RoomName");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					roomTextView = (TextView) findViewById(R.id.roomTextView);
					roomTextView.setText(cur_roomname);

					cur_switchname = "Select Switch";
					cur_switch = "xx";
					switchTextView = (TextView) findViewById(R.id.switchTextView);
					switchTextView.setText("Select Switch");
					dimmerRelativeLayout.setVisibility(RelativeLayout.GONE);
				}

			} else if (data.getStringExtra("type").equals("switch")) {
				String return_switch_id = data.getStringExtra("roomswitch_id");
				if (!return_switch_id.equals(cur_switch)) {
					cur_switch = return_switch_id;
					JSONObject objJSON;
					try {
						objJSON = new JSONObject(rawMasterJSON);
						cur_switchname = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getJSONArray("Switch").getJSONObject(Integer.parseInt(cur_switch)).getString("SwitchName");
						cur_switchtype = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getJSONArray("Switch").getJSONObject(Integer.parseInt(cur_switch)).getString("Type");
						if (cur_switchtype.equals("Dimmer")||cur_switchtype.equals("Fan")) {

							if (cur_switch_status.equals("ON")) {
								dimmerRelativeLayout.setVisibility(RelativeLayout.VISIBLE);
							} else {
								dimmerRelativeLayout.setVisibility(RelativeLayout.GONE);
							}
						} else {
							dimmerRelativeLayout.setVisibility(RelativeLayout.GONE);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					cur_switchname = "Select Switch";
					cur_switch = "xx";
				}
				switchTextView = (TextView) findViewById(R.id.switchTextView);
				switchTextView.setText(cur_switchname);

			}

		}

	}

}