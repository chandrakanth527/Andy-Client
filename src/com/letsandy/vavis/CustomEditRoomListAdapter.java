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
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

@SuppressLint({ "InflateParams", "InlinedApi" })
public class CustomEditRoomListAdapter extends BaseAdapter {
	private Context context;
	private String[] listText;
	private String[] listIcon;
	@SuppressWarnings("unused")
	private String[] favIcon;
	ViewHolder viewHolder;

	public CustomEditRoomListAdapter(Context context, String[] listText, String[] listIcon, String[] favIcon) {
		this.context = context;
		this.listText = listText;
		this.listIcon = listIcon;
		this.favIcon = favIcon;
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
			convertView = inflater.inflate(R.layout.editswitchsingle, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.Image = (ImageView) convertView.findViewById(R.id.switchImageView);
			viewHolder.Text = (TextView) convertView.findViewById(R.id.switchTextView);
			viewHolder.switchImageViewBackground = (LinearLayout) convertView.findViewById(R.id.switchImageViewBackground);
			viewHolder.editSwitchImageViewBackground = (LinearLayout) convertView.findViewById(R.id.editSwitchImageViewBackground);
			viewHolder.switchTextLinearLayout = (LinearLayout) convertView.findViewById(R.id.switchTextLinearLayout);
		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.id.editSwitchImageViewBackground);
		}

		viewHolder.Position = position;
		convertView.setTag(R.id.editSwitchImageViewBackground, viewHolder);
		viewHolder.Text.setText(listText[position]);

		String Icon = listIcon[position];
		int resID = context.getResources().getIdentifier(Icon, "drawable", context.getPackageName());
		viewHolder.Image.setImageResource(resID);

		viewHolder.switchImageViewBackground.setTag(position);
		viewHolder.editSwitchImageViewBackground.setTag(position);
		viewHolder.switchTextLinearLayout.setTag(position);

		viewHolder.switchTextLinearLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final int position = (Integer) v.getTag();
				String room_id = Integer.toString(position);
				Intent intent = new Intent(context, EditSwitchActivity.class);
				intent.putExtra("room_id", room_id);
				context.startActivity(intent);
			}
		});

		viewHolder.editSwitchImageViewBackground.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				final int position = (Integer) v.getTag();
				final Dialog dialog = new Dialog(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.customdialog);
				final EditText editTextView = (EditText) dialog.findViewById(R.id.editTextView);
				editTextView.setText(listText[position]);
				editTextView.setSelection(listText[position].length());

				RelativeLayout okRelativeLayout = (RelativeLayout) dialog.findViewById(R.id.okRelativeLayout);
				okRelativeLayout.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						listText[position] = editTextView.getText().toString();
						viewHolder.Text.setText(editTextView.getText().toString());

						String masterJSON = "masterJSON.cfg";
						try {
							FileInputStream fis = context.openFileInput(masterJSON);
							InputStreamReader isr = new InputStreamReader(fis);
							BufferedReader bufferedReader = new BufferedReader(isr);
							String rawMasterJSON;
							rawMasterJSON = bufferedReader.readLine();

							JSONObject objJSON = new JSONObject(rawMasterJSON);
							JSONArray arrJSON = objJSON.getJSONArray("Room");
							arrJSON.getJSONObject(position).remove("RoomName");
							arrJSON.getJSONObject(position).put("RoomName", listText[position]);
							rawMasterJSON = objJSON.toString();
							FileOutputStream outputStream = context.openFileOutput(masterJSON, Context.MODE_PRIVATE);
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

		viewHolder.switchImageViewBackground.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				Intent intent = new Intent(context, IconActivity.class);
				intent.putExtra("ListPosition", Integer.toString(position));
				((Activity) context).startActivityForResult(intent, 2);

			}

		});

		return convertView;

	}
}
