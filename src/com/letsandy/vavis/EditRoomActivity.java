package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.content.Context;
import android.content.Intent;

public class EditRoomActivity extends BaseActivity {
	ListView customListView;
	ArrayList<String> room_id = new ArrayList<String>();
	ArrayList<String> room_name = new ArrayList<String>();
	ArrayList<String> room_icon = new ArrayList<String>();
	ArrayList<String> fav_icon = new ArrayList<String>();

	CustomEditRoomListAdapter customEditRoomListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.customlist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);

		room_id.clear();
		room_name.clear();
		room_icon.clear();
		fav_icon.clear();
		String masterJSON = "masterJSON.cfg";
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
				if (arrJSON.getJSONObject(i).has("Fav")) {
					fav_icon.add(arrJSON.getJSONObject(i).getString("Fav"));
				} else {
					fav_icon.add("no");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		customListView = (ListView) findViewById(R.id.customListView);
		customEditRoomListAdapter = new CustomEditRoomListAdapter(this, room_name.toArray(new String[room_name.size()]), room_icon.toArray(new String[room_icon.size()]), fav_icon.toArray(new String[fav_icon.size()]));
		customListView.setAdapter(customEditRoomListAdapter);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (requestCode == 2) {

		if (resultCode == RESULT_OK) {
			int ListPosition = Integer.parseInt(data.getStringExtra("ListPosition"));
			String icon = data.getStringExtra("icon");
			room_icon.set(ListPosition, icon);

			String masterJSON = "masterJSON.cfg";
			try {
				FileInputStream fis = openFileInput(masterJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				String rawMasterJSON;
				rawMasterJSON = bufferedReader.readLine();

				JSONObject objJSON = new JSONObject(rawMasterJSON);
				JSONArray arrJSON = objJSON.getJSONArray("Room");
				arrJSON.getJSONObject(ListPosition).remove("RoomIcon");
				arrJSON.getJSONObject(ListPosition).put("RoomIcon", icon);
				rawMasterJSON = objJSON.toString();
				FileOutputStream outputStream = openFileOutput(masterJSON, Context.MODE_PRIVATE);
				outputStream.write(rawMasterJSON.getBytes());
				outputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

			customListView.setAdapter(null);
			customEditRoomListAdapter = new CustomEditRoomListAdapter(this, room_name.toArray(new String[room_name.size()]), room_icon.toArray(new String[room_icon.size()]), fav_icon.toArray(new String[fav_icon.size()]));
			customListView.setAdapter(customEditRoomListAdapter);

		}
	}
}
