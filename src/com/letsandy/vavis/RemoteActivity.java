package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.content.Intent;

public class RemoteActivity extends BaseActivity {
	GridView remoteGridView;
	ArrayList<String> remote_name = new ArrayList<String>();
	ArrayList<String> remote_icon = new ArrayList<String>();
	ArrayList<String> remote_code = new ArrayList<String>();
	ArrayList<String> remote_type = new ArrayList<String>();
	String masterJSON = "masterJSON.cfg";
	JSONArray arrRoomJSON;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.remote);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);

		remoteGridView = (GridView) findViewById(R.id.remoteGridView);

		try {
			FileInputStream fis = openFileInput(masterJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawMasterJSON;
			rawMasterJSON = bufferedReader.readLine();

			JSONObject objJSON = new JSONObject(rawMasterJSON);
			arrRoomJSON = objJSON.getJSONArray("Room");
			for (int i = 0; i < arrRoomJSON.length(); i++) {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		String remotesJSON = "remotesJSON.cfg";
		String remoteControlsJSON = "remoteControlsJSON.cfg";

		try {
			FileInputStream fis = openFileInput(remotesJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawRemotesJSON;
			String rawRemoteControlsJSON;

			rawRemotesJSON = bufferedReader.readLine();
			JSONObject objJSON = new JSONObject(rawRemotesJSON);
			JSONArray arrJSON = objJSON.getJSONArray("REMOTE");

			fis = openFileInput(remoteControlsJSON);
			isr = new InputStreamReader(fis);
			bufferedReader = new BufferedReader(isr);
			rawRemoteControlsJSON = bufferedReader.readLine();
			JSONObject objRemtoeJSON = new JSONObject(rawRemoteControlsJSON);
			JSONArray arrRemoteJSON = objRemtoeJSON.getJSONArray("REMOTECONTROLS");

			for (int i = 0; i < arrJSON.length(); i++) {

				remote_code.add(arrJSON.getJSONObject(i).getString("CODE"));
				int room_id = Integer.parseInt(arrJSON.getJSONObject(i).getString("CODE").substring(5, 6));
				int remote_id = Integer.parseInt(arrJSON.getJSONObject(i).getString("CODE").substring(8, 9));

				String room_name = arrRoomJSON.getJSONObject(room_id).getString("RoomName");
				remote_name.add(room_name + " - " + arrJSON.getJSONObject(i).getString("NAME"));
				remote_type.add(arrRemoteJSON.getJSONObject(remote_id).getString("type"));
				String remote_type = arrRemoteJSON.getJSONObject(remote_id).getString("type");

				if (remote_type.equals("TV")) {
					remote_icon.add("ic_media");
				} else if (remote_type.equals("CABLE-STB")) {
					remote_icon.add("ic_stb");
				} else if (remote_type.equals("AC")) {
					remote_icon.add("ic_ac");
				} else if (remote_type.equals("DVD-BD")) {
					remote_icon.add("ic_dvd");
				} else if (remote_type.equals("PRJCTR")) {
					remote_icon.add("ic_projector");
				} else if (remote_type.equals("KODI")) {
					remote_icon.add("ic_media");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		remoteGridView.setAdapter(new CustomRemoteGridAdapter(this, remote_name.toArray(new String[remote_name.size()]), remote_icon.toArray(new String[remote_icon.size()])));

		remoteGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), RemoteGenerateActivity.class);
				intent.putExtra("Code", remote_code.get(position));
				startActivity(intent);
			}

		});

	}
}
