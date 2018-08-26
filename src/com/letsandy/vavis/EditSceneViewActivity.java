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
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

@SuppressLint("NewApi")
public class EditSceneViewActivity extends BaseActivity {
	ListView customListView;
	updateCommandList updateCommandList;
	CustomEditSceneViewListAdapter customEditViewSceneListAdapter;
	ArrayList<String> scene_command = new ArrayList<String>();
	ArrayList<String> scene_command_text = new ArrayList<String>();
	ArrayList<String> scene_command_type = new ArrayList<String>();
	int scene_id;
	String scene_name = null;
	String Type = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.scenecommandslist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		scene_id = intent.getIntExtra("scene_id", 0);
		Type = intent.getStringExtra("Type");
		updateCommandList = new updateCommandList();
		IntentFilter intentUpdateCommandList = new IntentFilter("com.letsandy.updateCommandList");
		registerReceiver(updateCommandList, intentUpdateCommandList);

		String sceneJSON = "sceneJSON.cfg";
		try {
			FileInputStream fis = openFileInput(sceneJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawMasterJSON;
			rawMasterJSON = bufferedReader.readLine();

			JSONObject objJSON = new JSONObject(rawMasterJSON);
			JSONArray arrJSON = objJSON.getJSONArray("Scene");
			scene_name = arrJSON.getJSONObject(scene_id).getString("SceneName");

			JSONArray arrJSONCommands = arrJSON.getJSONObject(scene_id).getJSONArray("Commands");
			for (int i = 0; i < arrJSONCommands.length(); i++) {
				scene_command.add(arrJSONCommands.getJSONObject(i).getString("Command"));
				scene_command_type.add(arrJSONCommands.getJSONObject(i).getString("Type"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		customListView = (ListView) findViewById(R.id.customListView);
		customEditViewSceneListAdapter = new CustomEditSceneViewListAdapter(this, scene_id, scene_command.toArray(new String[scene_command.size()]), scene_command_type.toArray(new String[scene_command_type.size()]));
		customListView.setAdapter(customEditViewSceneListAdapter);
		final TextView sceneTextView = (TextView) findViewById(R.id.sceneTextView);
		sceneTextView.setText(scene_name);

		LinearLayout sceneTextViewLayout = (LinearLayout) findViewById(R.id.sceneTextViewLayout);
		sceneTextViewLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final Dialog dialog = new Dialog(EditSceneViewActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.customdialog);
				final EditText editTextView = (EditText) dialog.findViewById(R.id.editTextView);
				editTextView.setText(scene_name);
				editTextView.setSelection(scene_name.length());

				RelativeLayout okRelativeLayout = (RelativeLayout) dialog.findViewById(R.id.okRelativeLayout);
				okRelativeLayout.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						scene_name = editTextView.getText().toString();
						sceneTextView.setText(editTextView.getText().toString());

						String sceneJSON = "sceneJSON.cfg";
						try {
							FileInputStream fis = openFileInput(sceneJSON);
							InputStreamReader isr = new InputStreamReader(fis);
							BufferedReader bufferedReader = new BufferedReader(isr);
							String rawMasterJSON;
							rawMasterJSON = bufferedReader.readLine();

							JSONObject objJSON = new JSONObject(rawMasterJSON);
							JSONArray arrJSON = objJSON.getJSONArray("Scene");
							arrJSON.getJSONObject(scene_id).remove("SceneName");
							arrJSON.getJSONObject(scene_id).put("SceneName", scene_name);
							rawMasterJSON = objJSON.toString();

							FileOutputStream outputStream = openFileOutput(sceneJSON, Context.MODE_PRIVATE);
							outputStream.write(rawMasterJSON.getBytes());
							outputStream.close();

						} catch (Exception e) {
							e.printStackTrace();
						}
						dialog.dismiss();
					}
				});

				RelativeLayout cancelRelativeLayout = (RelativeLayout) dialog.findViewById(R.id.cancelRelativeLayout);
				cancelRelativeLayout.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.show();
			}

		});

		RelativeLayout deleteRelativeLayout = (RelativeLayout) findViewById(R.id.deleteRelativeLayout);
		deleteRelativeLayout.setOnClickListener(new OnClickListener() {
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
					arrJSON.remove(scene_id);
					rawMasterJSON = objJSON.toString();
					FileOutputStream outputStream = openFileOutput(sceneJSON, Context.MODE_PRIVATE);
					outputStream.write(rawMasterJSON.getBytes());
					outputStream.close();
					finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		RelativeLayout addRelativeLayout = (RelativeLayout) findViewById(R.id.addRelativeLayout);
		addRelativeLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), AddEventActivity.class);
				intent.putExtra("Type", "New");
				intent.putExtra("SceneId", scene_id);
				intent.putExtra("command_id", 0);
				startActivity(intent);
			}

		});

	}

	public class updateCommandList extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ArrayList<String> scene_command = new ArrayList<String>();
			ArrayList<String> scene_command_text = new ArrayList<String>();
			ArrayList<String> scene_command_type = new ArrayList<String>();
			String sceneJSON = "sceneJSON.cfg";
			try {
				FileInputStream fis = openFileInput(sceneJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				String rawMasterJSON;
				rawMasterJSON = bufferedReader.readLine();

				JSONObject objJSON = new JSONObject(rawMasterJSON);
				JSONArray arrJSON = objJSON.getJSONArray("Scene");
				scene_name = arrJSON.getJSONObject(scene_id).getString("SceneName");

				JSONArray arrJSONCommands = arrJSON.getJSONObject(scene_id).getJSONArray("Commands");

				JSONObject masterJSON = null;
				String masterJSON1 = "masterJSON.cfg";
				String rawMasterJSON1 = null;
				try {
					FileInputStream fis1 = openFileInput(masterJSON1);
					InputStreamReader isr1 = new InputStreamReader(fis1);
					BufferedReader bufferedReader1 = new BufferedReader(isr1);
					rawMasterJSON1 = bufferedReader1.readLine();

				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					masterJSON = new JSONObject(rawMasterJSON1);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				for (int i = 0; i < arrJSONCommands.length(); i++) {
					scene_command.add(arrJSONCommands.getJSONObject(i).getString("Command"));
					scene_command_type.add(arrJSONCommands.getJSONObject(i).getString("Type"));

					String cur_room = arrJSONCommands.getJSONObject(i).getString("Command").substring(1, 3);
					String cur_switch = arrJSONCommands.getJSONObject(i).getString("Command").substring(4, 6);
					String cur_switch_status = arrJSONCommands.getJSONObject(i).getString("Command").substring(6, 8);
					String cur_roomname = masterJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getString("RoomName");
					String cur_switchname = masterJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getJSONArray("Switch").getJSONObject(Integer.parseInt(cur_switch)).getString("SwitchName");

					if (cur_switch_status.equals("ON")) {
						scene_command_text.add(cur_roomname + " - " + cur_switchname + " - On");
					} else if (cur_switch_status.equals("OF")) {
						scene_command_text.add(cur_roomname + " - " + cur_switchname + " - Off");
					} else {
						scene_command_text.add(cur_roomname + " - " + cur_switchname + " - " + cur_switch_status);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			customListView = (ListView) findViewById(R.id.customListView);
			customEditViewSceneListAdapter = new CustomEditSceneViewListAdapter(getApplicationContext(), scene_id, scene_command_text.toArray(new String[scene_command_text.size()]), scene_command_type.toArray(new String[scene_command_type.size()]));
			customListView.setAdapter(customEditViewSceneListAdapter);
		}
	}

	public void onResume() {
		super.onResume();
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("com.letsandy.updateCommandList");
		sendBroadcast(broadcastIntent);
	}

	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(updateCommandList);
	}

}
