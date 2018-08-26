package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditAddRemoteActivity extends BaseActivity {
	LinearLayout Masterlayout;
	LinearLayout DynamicSwitch;
	EditText remoteNameEditText;
	TextView roomTextView;
	TextView remoteTextView;
	TextView zmoteTextView;
	RelativeLayout roomLayout;
	RelativeLayout remoteLayout;
	RelativeLayout zmoteLayout;
	RelativeLayout deleteRelativeLayout;
	RelativeLayout saveRelativeLayout;
	String remoteName = null;
	String command = null;
	String status;
	String masterJSON = "masterJSON.cfg";
	String rawMasterJSON = null;
	String remotesJSON = "remotesJSON.cfg";
	String rawRemotesJSON = null;
	String remoteControlsJSON = "remoteControlsJSON.cfg";
	String rawRemoteControlsJSON = null;
	String zmoteJSON = "zmoteJSON.cfg";
	String rawZmoteJSON = null;
	String cur_room;
	String cur_remote;
	String cur_zmote;
	String cur_name;
	String cur_roomname;
	String cur_remotename;
	String cur_zmotename;
	String Type = null;
	ArrayList<String> room_raw;
	ArrayList<String> remote_raw;
	int remotePos;
	int resID1;
	int resID2;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.addremote);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		Type = intent.getStringExtra("Type");
		remotePos = intent.getIntExtra("remotePos", 0);
		room_raw = new ArrayList<String>();
		remote_raw = new ArrayList<String>();

		try {
			FileInputStream fis = openFileInput(remotesJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			rawRemotesJSON = bufferedReader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FileInputStream fis = openFileInput(remoteControlsJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			rawRemoteControlsJSON = bufferedReader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FileInputStream fis = openFileInput(zmoteJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			rawZmoteJSON = bufferedReader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Type.equals("New")) {
			cur_room = "xx";
			cur_remote = "xx";
			cur_zmote = "xx";

			cur_roomname = "Select Room";
			cur_remotename = "Select Remote";
			cur_zmotename = "Select IR Controller";

			try {
				FileInputStream fis = openFileInput(masterJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				rawMasterJSON = bufferedReader.readLine();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			cur_name = "";
			cur_roomname = "";
			cur_remotename = "";
			cur_zmotename = "";

			try {
				JSONObject objJSON = new JSONObject(rawRemotesJSON);
				cur_name = objJSON.getJSONArray("REMOTE").getJSONObject(remotePos).getString("NAME");
				command = objJSON.getJSONArray("REMOTE").getJSONObject(remotePos).getString("CODE");
			} catch (Exception e) {
				e.printStackTrace();
			}
			cur_zmote = command.substring(1, 3);
			cur_room = command.substring(4, 6);
			cur_remote = command.substring(7, 9);

			try {
				FileInputStream fis = openFileInput(masterJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				rawMasterJSON = bufferedReader.readLine();
				JSONObject objJSON;
				objJSON = new JSONObject(rawMasterJSON);
				cur_roomname = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getString("RoomName");

				objJSON = new JSONObject(rawRemoteControlsJSON);
				cur_remotename = objJSON.getJSONArray("REMOTECONTROLS").getJSONObject(Integer.parseInt(cur_remote)).getString("brand") + " - " + objJSON.getJSONArray("REMOTECONTROLS").getJSONObject(Integer.parseInt(cur_remote)).getString("type");

				objJSON = new JSONObject(rawZmoteJSON);
				cur_zmotename = objJSON.getJSONArray("ZMOTE").getJSONObject(Integer.parseInt(cur_zmote)).getString("IP");

			} catch (Exception e) {
				e.printStackTrace();
			}

			remoteNameEditText = (EditText) findViewById(R.id.remoteNameEditText);
			remoteNameEditText.append(cur_name);

		}

		roomTextView = (TextView) findViewById(R.id.roomTextView);
		roomTextView.setText(cur_roomname);

		remoteTextView = (TextView) findViewById(R.id.remoteTextView);
		remoteTextView.setText(cur_remotename);

		zmoteTextView = (TextView) findViewById(R.id.zmoteTextView);
		zmoteTextView.setText(cur_zmotename);

		roomLayout = (RelativeLayout) findViewById(R.id.roomLayout);
		roomLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SelectRoomSwitchActivity.class);
				intent.putExtra("type", "room");
				intent.putExtra("room_id", cur_room);
				startActivityForResult(intent, 2);
			}
		});

		remoteLayout = (RelativeLayout) findViewById(R.id.remoteLayout);
		remoteLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SelectRemoteActivity.class);
				intent.putExtra("remote_id", cur_remote);
				startActivityForResult(intent, 3);
			}
		});

		zmoteLayout = (RelativeLayout) findViewById(R.id.zmoteLayout);
		zmoteLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(), SelectZmoteActivity.class);
				intent.putExtra("zmote_id", cur_zmote);
				startActivityForResult(intent, 4);

			}
		});

		deleteRelativeLayout = (RelativeLayout) findViewById(R.id.deleteRelativeLayout);
		deleteRelativeLayout.setClickable(true);
		deleteRelativeLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!Type.equals("New")) {
					JSONObject objJSON;
					try {
						objJSON = new JSONObject(rawRemotesJSON);
						objJSON.getJSONArray("REMOTE").remove(remotePos);

						rawRemotesJSON = objJSON.toString();
						FileOutputStream outputStream;
						try {
							outputStream = openFileOutput(remotesJSON, Context.MODE_PRIVATE);
							outputStream.write(rawRemotesJSON.getBytes());
							outputStream.close();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					Toast.makeText(getApplicationContext(), "Remote Deleted", Toast.LENGTH_SHORT).show();
				}
				finish();

			}
		});

		saveRelativeLayout = (RelativeLayout) findViewById(R.id.saveRelativeLayout);
		saveRelativeLayout.setClickable(true);
		saveRelativeLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				remoteNameEditText = (EditText) findViewById(R.id.remoteNameEditText);
				String remoteName = remoteNameEditText.getText().toString();

				if (remoteName.equals("")) {
					Toast.makeText(getApplicationContext(), "Remote Name is Empty", Toast.LENGTH_SHORT).show();
				} else if (cur_room.equals("xx")) {
					Toast.makeText(getApplicationContext(), "Select Room", Toast.LENGTH_SHORT).show();
				} else if (cur_remote.equals("xx")) {
					Toast.makeText(getApplicationContext(), "Select Remote", Toast.LENGTH_SHORT).show();
				} else if (cur_zmote.equals("xx")) {
					Toast.makeText(getApplicationContext(), "Select Zmote", Toast.LENGTH_SHORT).show();
				} else {

					String command_new = "Z" + String.format("%02d", Integer.parseInt(cur_zmote)) + "R" + String.format("%02d", Integer.parseInt(cur_room)) + "C" + String.format("%02d", Integer.parseInt(cur_remote));
					JSONObject objJSON;
					JSONObject newJSON = new JSONObject();
					try {
						objJSON = new JSONObject(rawRemotesJSON);
						if (Type.equals("New")) {
							newJSON.put("NAME", remoteName);
							newJSON.put("CODE", command_new);
							objJSON.getJSONArray("REMOTE").put(newJSON);
						} else {
							objJSON.getJSONArray("REMOTE").getJSONObject(remotePos).remove("NAME");
							objJSON.getJSONArray("REMOTE").getJSONObject(remotePos).put("NAME", remoteName);
							objJSON.getJSONArray("REMOTE").getJSONObject(remotePos).remove("CODE");
							objJSON.getJSONArray("REMOTE").getJSONObject(remotePos).put("CODE", command_new);
						}
						rawRemotesJSON = objJSON.toString();

						FileOutputStream outputStream;
						try {
							outputStream = openFileOutput(remotesJSON, Context.MODE_PRIVATE);
							outputStream.write(rawRemotesJSON.getBytes());
							outputStream.close();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (Type.equals("New")) {
						Toast.makeText(getApplicationContext(), "Remote Added", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(), "Remote Updated", Toast.LENGTH_SHORT).show();
					}
					finish();
				}
			}
		});

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
				}
			}
		}

		if (requestCode == 3) {

			String return_remote_id = data.getStringExtra("remote_id");

			if (!return_remote_id.equals(cur_remote)) {
				cur_remote = return_remote_id;
				JSONObject objJSON;
				try {
					objJSON = new JSONObject(rawRemoteControlsJSON);
					cur_remotename = objJSON.getJSONArray("REMOTECONTROLS").getJSONObject(Integer.parseInt(return_remote_id)).getString("brand") + " - " + objJSON.getJSONArray("REMOTECONTROLS").getJSONObject(Integer.parseInt(return_remote_id)).getString("type");
					;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				remoteTextView = (TextView) findViewById(R.id.remoteTextView);
				remoteTextView.setText(cur_remotename);
			}
		}

		if (requestCode == 4) {

			String return_zmote_id = data.getStringExtra("zmote_id");
			if (!return_zmote_id.equals(cur_zmote)) {
				cur_zmote = return_zmote_id;

				JSONObject objJSON;
				try {
					objJSON = new JSONObject(rawZmoteJSON);
					cur_zmotename = objJSON.getJSONArray("ZMOTE").getJSONObject(Integer.parseInt(cur_zmote)).getString("IP");
					;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				zmoteTextView = (TextView) findViewById(R.id.zmoteTextView);
				zmoteTextView.setText(cur_zmotename);

			}
		}

	}

}