package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;

public class RoomFullActivity extends BaseActivity {
	String room_id;
	String masterJSON = "masterJSON.cfg";
	String room_name;
	GridView roomFullGridView;
	ArrayList<String> remote_name = new ArrayList<String>();
	ArrayList<String> remote_icon = new ArrayList<String>();
	ArrayList<String> options_name = new ArrayList<String>();
	ArrayList<String> options_icon = new ArrayList<String>();
	ArrayList<String> options_type = new ArrayList<String>();
	ArrayList<String> options_parameter = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.room_full);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		room_id = intent.getStringExtra("room_id");

		ImageView roomImage = (ImageView) findViewById(R.id.roomImage);
		String pathName = Environment.getExternalStorageDirectory() + File.separator + "vavis" + File.separator + "Room" + room_id + ".jpg";
		File file = new File(pathName);

		final ImageView switchOffImage = (ImageView) findViewById(R.id.switchCompleteOff);
		switchOffImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ObjectAnimator colorFade = ObjectAnimator.ofObject(switchOffImage, "colorFilter", new ArgbEvaluator(), 0xff00a6d6, 0x00000000);
				colorFade.setDuration(700);
				colorFade.start();
				messagePublish("request/switchAllOff", "R" + String.format("%02d", Integer.parseInt(room_id)) + "OF");
			}
		});

		try {
			FileInputStream fis = openFileInput(masterJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawMasterJSON;
			rawMasterJSON = bufferedReader.readLine();
			JSONObject objJSON = new JSONObject(rawMasterJSON);
			room_name = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(room_id)).getString("RoomName");

		} catch (Exception e) {
			e.printStackTrace();
		}
		TextView roomNameTextView = (TextView) findViewById(R.id.roomName);
		roomNameTextView.setText(room_name);

		String configJSON = "configJSON.cfg";
		String rawConfigJSON;
		JSONObject objStatusJSON;
		Boolean switchOptions = false;
		Boolean remoteOptions = false;

		try {
			FileInputStream fis = openFileInput(configJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			rawConfigJSON = bufferedReader.readLine();
			objStatusJSON = new JSONObject(rawConfigJSON).getJSONObject("Options");
			if (objStatusJSON.has("VAVIS") && objStatusJSON.getString("VAVIS").equals("yes")) {
				switchOptions = true;
			}

			if (objStatusJSON.has("SCENE") && objStatusJSON.getString("SCENE").equals("yes")) {

			}
			if (objStatusJSON.has("CCTV") && objStatusJSON.getString("CCTV").equals("yes")) {
			}

			if (objStatusJSON.has("ZMOTE") && objStatusJSON.getString("ZMOTE").equals("yes")) {
				remoteOptions = true;
			}
			if (objStatusJSON.has("SECURITY") && objStatusJSON.getString("SECURITY").equals("yes")) {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (switchOptions) {
			int Normal_Flag = 0;
			int Fan_Flag = 0;
			int Curtain_Flag = 0;
			int Scene_Flag = 0;
			try {
				FileInputStream fis = openFileInput(masterJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				String rawMasterJSON;
				rawMasterJSON = bufferedReader.readLine();
				JSONObject objJSON = new JSONObject(rawMasterJSON);
				JSONArray arrJSON = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(room_id)).getJSONArray("Switch");
				room_name = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(room_id)).getString("RoomName");
				for (int i = 0; i < arrJSON.length(); i++) {
					if (arrJSON.getJSONObject(i).getString("Type").equals("Normal") && Normal_Flag == 0) {
						Normal_Flag = 1;
					} else if (arrJSON.getJSONObject(i).getString("Type").equals("Fan") && Fan_Flag == 0) {
						Fan_Flag = 1;
					} else if (arrJSON.getJSONObject(i).getString("Type").equals("Curtain") && Curtain_Flag == 0) {
						Curtain_Flag = 1;
					} else if (arrJSON.getJSONObject(i).getString("Type").equals("Scene") && Scene_Flag == 0) {
						Scene_Flag = 1;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			options_name.add("All");
			options_icon.add("ic_all");
			options_type.add("Switch");
			options_parameter.add("All");
			if (Scene_Flag == 1) {
				options_name.add("Scene");
				options_icon.add("ic_scene");
				options_type.add("Switch");
				options_parameter.add("Scene");
			}
			if (Normal_Flag == 1) {
				options_name.add("Lights");
				options_icon.add("ic_switch");
				options_type.add("Switch");
				options_parameter.add("Normal");
			}
			if (Fan_Flag == 1) {
				options_name.add("Fan");
				options_icon.add("ic_fan");
				options_type.add("Switch");
				options_parameter.add("Fan");
			}

			if (Curtain_Flag == 1) {
				options_name.add("Curtain");
				options_icon.add("ic_curtain");
				options_type.add("Switch");
				options_parameter.add("Curtain");
			}
		}
		if (remoteOptions) {
			String remotesJSON = "remotesJSON.cfg";
			String remoteControlsJSON = "remoteControlsJSON.cfg";
			try {
				FileInputStream fis = openFileInput(remotesJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				String rawRemotesJSON;
				rawRemotesJSON = bufferedReader.readLine();
				JSONObject objJSON = new JSONObject(rawRemotesJSON);
				JSONArray arrJSON = objJSON.getJSONArray("REMOTE");

				fis = openFileInput(remoteControlsJSON);
				isr = new InputStreamReader(fis);
				bufferedReader = new BufferedReader(isr);
				String rawRemoteControlsJSON;
				rawRemoteControlsJSON = bufferedReader.readLine();

				JSONObject objJSON1 = new JSONObject(rawRemoteControlsJSON);
				JSONArray arrJSON1 = objJSON1.getJSONArray("REMOTECONTROLS");

				for (int i = 0; i < arrJSON.length(); i++) {
					int Room_id = Integer.parseInt(arrJSON.getJSONObject(i).getString("CODE").substring(4, 6));
					if (Room_id == Integer.parseInt(room_id)) {
						options_type.add("Remote");
						options_parameter.add(arrJSON.getJSONObject(i).getString("CODE"));
						options_name.add(arrJSON.getJSONObject(i).getString("NAME"));
						int remote = Integer.parseInt(arrJSON.getJSONObject(i).getString("CODE").substring(7, 9));
						String remote_type = arrJSON1.getJSONObject(remote).getString("type");
						if (remote_type.equals("TV")) {
							options_icon.add("ic_media");
						} else if (remote_type.equals("CABLE-STB")) {
							options_icon.add("ic_stb");
						} else if (remote_type.equals("AC")) {
							options_icon.add("ic_ac");
						} else if (remote_type.equals("DVD-BD")) {
							options_icon.add("ic_dvd");
						} else if (remote_type.equals("PRJCTR")) {
							options_icon.add("ic_projector");
						} else if (remote_type.equals("KODI")) {
							options_icon.add("ic_media");
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		roomFullGridView = (GridView) findViewById(R.id.roomAppsGridView);
		roomFullGridView.setAdapter(new CustomRoomFullGridAdapter(this, options_name.toArray(new String[options_name.size()]), options_icon.toArray(new String[options_icon.size()])));
		roomFullGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				options_function(options_type.get(position), options_parameter.get(position));
			}
		});

		SharedPreferences prefs = getSharedPreferences("RoomUnique", 0);
		String RoomUnique = prefs.getString("Room" + room_id, "123");
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int height = metrics.heightPixels;
		int width = metrics.widthPixels;
		if (file.exists()) {
			Glide.with(getApplicationContext()).load(file).diskCacheStrategy(DiskCacheStrategy.NONE).dontAnimate().dontTransform().override(width, height).signature(new StringSignature(RoomUnique)).into(roomImage);
		} else {
			Glide.with(getApplicationContext()).load(R.drawable.home).diskCacheStrategy(DiskCacheStrategy.NONE).dontAnimate().dontTransform().override(width, height).signature(new StringSignature(RoomUnique)).into(roomImage);
		}

	}

	public void messagePublish(String TOPIC, String MESSAGE) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("com.letsandy.publishMQTT");
		broadcastIntent.putExtra("TOPIC", TOPIC);
		broadcastIntent.putExtra("MESSAGE", MESSAGE);
		sendBroadcast(broadcastIntent);
	}

	public void options_function(String options_type, String options_parameter) {
		if (options_type == "Switch") {
			Intent intent = new Intent(getApplicationContext(), SwitchActivity.class);
			intent.putExtra("room_id", room_id);
			intent.putExtra("type", options_parameter);
			startActivity(intent);
		} else if (options_type == "Remote") {
			Intent intent = new Intent(getApplicationContext(), RemoteGenerateActivity.class);
			intent.putExtra("Code", options_parameter);
			startActivity(intent);
		}
	}
}
