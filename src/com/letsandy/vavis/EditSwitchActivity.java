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

public class EditSwitchActivity extends BaseActivity {
	ListView customListView;
	ArrayList<String> switch_id = new ArrayList<String>();
	ArrayList<String> switch_name = new ArrayList<String>();
	ArrayList<String> switch_icon = new ArrayList<String>();
	ArrayList<String> fav_icon = new ArrayList<String>();
	CustomEditSwitchListAdapter customEditSwitchListAdapter;
	String room_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.customlist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		room_id = intent.getStringExtra("room_id");
		String masterJSON = "masterJSON.cfg";
		try {
			FileInputStream fis = openFileInput(masterJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawMasterJSON;
			rawMasterJSON = bufferedReader.readLine();

			JSONObject objJSON = new JSONObject(rawMasterJSON);
			JSONArray arrJSON = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(room_id)).getJSONArray("Switch");
			for (int i = 0; i < arrJSON.length(); i++) {

				switch_id.add(Integer.toString(i));
				switch_name.add(arrJSON.getJSONObject(i).getString("SwitchName"));
				switch_icon.add(arrJSON.getJSONObject(i).getString("SwitchIcon"));
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
		customEditSwitchListAdapter = new CustomEditSwitchListAdapter(this, switch_name.toArray(new String[switch_name.size()]), switch_icon.toArray(new String[switch_icon.size()]), fav_icon.toArray(new String[fav_icon.size()]), room_id);
		customListView.setAdapter(customEditSwitchListAdapter);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (requestCode == 2) {

		if (resultCode == RESULT_OK) {
			int ListPosition = Integer.parseInt(data.getStringExtra("ListPosition"));
			String icon = data.getStringExtra("icon");
			switch_icon.set(ListPosition, icon);

			String masterJSON = "masterJSON.cfg";
			try {
				FileInputStream fis = openFileInput(masterJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				String rawMasterJSON;
				rawMasterJSON = bufferedReader.readLine();

				JSONObject objJSON = new JSONObject(rawMasterJSON);
				JSONArray arrJSON = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(room_id)).getJSONArray("Switch");
				arrJSON.getJSONObject(ListPosition).remove("SwitchIcon");
				arrJSON.getJSONObject(ListPosition).put("SwitchIcon", icon);
				rawMasterJSON = objJSON.toString();
				FileOutputStream outputStream = openFileOutput(masterJSON, Context.MODE_PRIVATE);
				outputStream.write(rawMasterJSON.getBytes());
				outputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

			customListView.setAdapter(null);
			customEditSwitchListAdapter = new CustomEditSwitchListAdapter(this, switch_name.toArray(new String[switch_name.size()]), switch_icon.toArray(new String[switch_icon.size()]), fav_icon.toArray(new String[fav_icon.size()]), room_id);
			customListView.setAdapter(customEditSwitchListAdapter);

		}
	}
}
