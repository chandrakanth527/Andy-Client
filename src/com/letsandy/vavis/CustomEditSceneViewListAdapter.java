package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

@SuppressLint({ "InflateParams", "InlinedApi", "NewApi" })
public class CustomEditSceneViewListAdapter extends BaseAdapter {
	private Context context;
	private String[] listText;
	@SuppressWarnings("unused")
	private String[] listType;
	private int scene_id;
	ViewHolder viewHolder;

	public CustomEditSceneViewListAdapter(Context context, int scene_id, String[] listText, String[] listType) {
		this.context = context;
		this.listText = listText;
		this.listType = listType;
		this.scene_id = scene_id;
	}

	@Override
	public int getCount() {
		return listText.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("ViewTag") public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.editswitchcommandsingle, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.Text = (TextView) convertView.findViewById(R.id.switchTextView);
			viewHolder.switchTextLinearLayout = (LinearLayout) convertView.findViewById(R.id.switchTextLinearLayout);
			viewHolder.deleteImageViewBackground = (LinearLayout) convertView.findViewById(R.id.deleteImageViewBackground);
			viewHolder.editImageViewBackground = (LinearLayout) convertView.findViewById(R.id.editImageViewBackground);
		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.id.switchTextLinearLayout);
		}

		viewHolder.Position = position;
		convertView.setTag(R.id.switchTextLinearLayout, viewHolder);
		viewHolder.Text.setText(listText[position]);

		viewHolder.switchTextLinearLayout.setTag(position);
		viewHolder.deleteImageViewBackground.setTag(position);
		viewHolder.editImageViewBackground.setTag(position);

		viewHolder.deleteImageViewBackground.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				String sceneJSON = "sceneJSON.cfg";
				try {
					FileInputStream fis = context.openFileInput(sceneJSON);
					InputStreamReader isr = new InputStreamReader(fis);
					BufferedReader bufferedReader = new BufferedReader(isr);
					String rawMasterJSON;
					rawMasterJSON = bufferedReader.readLine();

					JSONObject objJSON = new JSONObject(rawMasterJSON);
					JSONArray arrJSON = objJSON.getJSONArray("Scene");
					JSONArray arrJSONCommands = arrJSON.getJSONObject(scene_id).getJSONArray("Commands");
					arrJSONCommands.remove(position);
					rawMasterJSON = objJSON.toString();
					FileOutputStream outputStream = context.openFileOutput(sceneJSON, Context.MODE_PRIVATE);
					outputStream.write(rawMasterJSON.getBytes());
					outputStream.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
				updateResults();
			}
		});

		viewHolder.editImageViewBackground.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				Intent intent = new Intent(context, AddEventActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("Type", "Edit");
				intent.putExtra("SceneId", scene_id);
				intent.putExtra("command_id", position);
				context.startActivity(intent);
			}
		});

		return convertView;

	}

	public void updateResults() {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("com.letsandy.updateCommandList");
		context.sendBroadcast(broadcastIntent);
	}
}