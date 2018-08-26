package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

@SuppressLint("NewApi")
public class EditSceneActivity extends BaseActivity {
	ListView customListView;
	ArrayList<String> scene_id = new ArrayList<String>();
	ArrayList<String> scene_name = new ArrayList<String>();
	ArrayList<String> fav_icon = new ArrayList<String>();
	CustomEditSceneListAdapter customEditSceneListAdapter;
	String Add_Type = "Old";
	int Temp_Scene_Id = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.scenelist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);

		String sceneJSON = "sceneJSON.cfg";
		try {
			FileInputStream fis = openFileInput(sceneJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawMasterJSON;
			rawMasterJSON = bufferedReader.readLine();

			JSONObject objJSON = new JSONObject(rawMasterJSON);
			JSONArray arrJSON = objJSON.getJSONArray("Scene");
			for (int i = 0; i < arrJSON.length(); i++) {

				scene_id.add(Integer.toString(i));
				scene_name.add(arrJSON.getJSONObject(i).getString("SceneName"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		customListView = (ListView) findViewById(R.id.customListView);
		customEditSceneListAdapter = new CustomEditSceneListAdapter(this, scene_name.toArray(new String[scene_name.size()]));
		customListView.setAdapter(customEditSceneListAdapter);

		RelativeLayout addRelativeLayout = (RelativeLayout) findViewById(R.id.addRelativeLayout);
		addRelativeLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String sceneJSON = "sceneJSON.cfg";
				try {
					FileInputStream fis = openFileInput(sceneJSON);
					InputStreamReader isr = new InputStreamReader(fis);
					BufferedReader bufferedReader = new BufferedReader(isr);
					String rawMasterJSON;
					rawMasterJSON = bufferedReader.readLine();
					JSONObject objJSON = new JSONObject(rawMasterJSON);
					JSONArray arrJSON = objJSON.getJSONArray("Scene");
					JSONObject newJSON = new JSONObject();
					JSONArray newJSONArray = new JSONArray();
					newJSON.put("SceneName", "New Scene");
					newJSON.put("SceneIcon", "ic_scene");
					newJSON.put("Commands", newJSONArray);
					arrJSON.put(newJSON);
					rawMasterJSON = objJSON.toString();
					FileOutputStream outputStream = openFileOutput(sceneJSON, Context.MODE_PRIVATE);
					outputStream.write(rawMasterJSON.getBytes());
					outputStream.close();
					Temp_Scene_Id = arrJSON.length() - 1;
					Add_Type = "New";
					Intent intent = new Intent(getApplicationContext(), EditSceneViewActivity.class);
					intent.putExtra("scene_id", arrJSON.length() - 1);
					intent.putExtra("Type", "New");
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void onResume() {
		super.onResume();
		ArrayList<String> scene_id = new ArrayList<String>();
		ArrayList<String> scene_name = new ArrayList<String>();

		if (Add_Type.equals("New")) {
			String sceneJSON = "sceneJSON.cfg";
			try {
				FileInputStream fis = openFileInput(sceneJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				String rawMasterJSON;
				rawMasterJSON = bufferedReader.readLine();
				JSONObject objJSON = new JSONObject(rawMasterJSON);
				JSONArray arrJSON = objJSON.getJSONArray("Scene");

				JSONArray arrJSONCommands = arrJSON.getJSONObject(Temp_Scene_Id).getJSONArray("Commands");
				if (arrJSONCommands.length() == 0) {
					arrJSON.remove(Temp_Scene_Id);
					rawMasterJSON = objJSON.toString();
					FileOutputStream outputStream = openFileOutput(sceneJSON, Context.MODE_PRIVATE);
					outputStream.write(rawMasterJSON.getBytes());
					outputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Add_Type = "Old";
			Temp_Scene_Id = -1;
		}

		try {
			String sceneJSON = "sceneJSON.cfg";
			FileInputStream fis = openFileInput(sceneJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawMasterJSON;
			rawMasterJSON = bufferedReader.readLine();
			JSONObject objJSON = new JSONObject(rawMasterJSON);
			JSONArray arrJSON = objJSON.getJSONArray("Scene");

			for (int i = 0; i < arrJSON.length(); i++) {
				scene_id.add(Integer.toString(i));
				scene_name.add(arrJSON.getJSONObject(i).getString("SceneName"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		customListView.setAdapter(null);
		customEditSceneListAdapter = new CustomEditSceneListAdapter(this, scene_name.toArray(new String[scene_name.size()]));
		customListView.setAdapter(customEditSceneListAdapter);
	}

}
