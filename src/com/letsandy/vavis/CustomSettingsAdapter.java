package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

@SuppressLint({ "InflateParams", "InlinedApi", "ViewTag" })
public class CustomSettingsAdapter extends BaseAdapter {
	private Context context;
	private String[] listText;
	private String[] listIcon;
	ViewHolder viewHolder;

	public CustomSettingsAdapter(Context context, String[] listText, String[] listIcon) {
		this.context = context;
		this.listText = listText;
		this.listIcon = listIcon;
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

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.settingsingle, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.Image = (ImageView) convertView.findViewById(R.id.settingImageView);
			viewHolder.Text = (TextView) convertView.findViewById(R.id.settingTextView);
			viewHolder.switchImageViewBackground = (LinearLayout) convertView.findViewById(R.id.settingImageViewBackground);
			viewHolder.switchTextLinearLayout = (LinearLayout) convertView.findViewById(R.id.settingTextLinearLayout);
			viewHolder.LinearLayout = (LinearLayout) convertView.findViewById(R.id.settingLayout);
		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.id.settingImageViewBackground);
		}

		viewHolder.Position = position;
		convertView.setTag(R.id.settingImageViewBackground, viewHolder);
		viewHolder.Text.setText(listText[position]);

		String Icon = listIcon[position];
		int resID = context.getResources().getIdentifier(Icon, "drawable", context.getPackageName());
		viewHolder.Image.setImageResource(resID);

		viewHolder.LinearLayout.setTag(position);
		viewHolder.LinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				String setting_type = listText[position];
				if (setting_type.equals("Add Voice Commands")) {
					Intent intent = new Intent(context, VoiceActivity.class);
					context.startActivity(intent);

				} else if (setting_type.equals("Edit Switches")) {
					Intent intent = new Intent(context, EditRoomActivity.class);
					context.startActivity(intent);

				} else if (setting_type.equals("Edit Remotes")) {
					Intent intent = new Intent(context, EditRemoteActivity.class);
					context.startActivity(intent);

				} else if (setting_type.equals("Infrared Controller Info")) {
					Intent intent = new Intent(context, GenericActivity.class);
					ArrayList<String> HEAD = new ArrayList<String>();
					ArrayList<String> DESC = new ArrayList<String>();
					String zmoteJSON = "zmoteJSON.cfg";
					String rawZmoteJSON = "";
					try {
						FileInputStream fis = context.openFileInput(zmoteJSON);
						InputStreamReader isr = new InputStreamReader(fis);
						BufferedReader bufferedReader = new BufferedReader(isr);
						rawZmoteJSON = bufferedReader.readLine();
						JSONObject objJSON = new JSONObject(rawZmoteJSON);
						JSONArray arrJSON = objJSON.getJSONArray("ZMOTE");
						for (int i = 0; i < arrJSON.length(); i++) {
							HEAD.add(arrJSON.getJSONObject(i).getString("IP"));
							DESC.add(arrJSON.getJSONObject(i).getString("UID"));
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

					intent.putExtra("HEAD", HEAD);
					intent.putExtra("DESC", DESC);
					context.startActivity(intent);
				} else if (setting_type.equals("Push Configuration")) {
					Intent intent = new Intent(context, ConfigSyncActivity.class);
					intent.putExtra("type", "push");
					context.startActivity(intent);
				} else if (setting_type.equals("Pull Configuration")) {
					Intent intent = new Intent(context, ConfigSyncActivity.class);
					intent.putExtra("type", "pull");
					context.startActivity(intent);
				} else if (setting_type.equals("Reset Application")) {

					final Dialog dialog = new Dialog(context);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.customcheckdialog);

					RelativeLayout okRelativeLayout = (RelativeLayout) dialog.findViewById(R.id.okRelativeLayout);
					okRelativeLayout.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							Intent intent = new Intent(context, SetupActivity.class);
							context.startActivity(intent);
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
			}
		});

		return convertView;

	}
}
