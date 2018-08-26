package com.letsandy.vavis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

@SuppressLint({ "InflateParams", "InlinedApi" })
public class CustomEditSceneListAdapter extends BaseAdapter {
	private Context context;
	private String[] listText;
	ViewHolder viewHolder;

	public CustomEditSceneListAdapter(Context context, String[] listText) {
		this.context = context;
		this.listText = listText;
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
			convertView = inflater.inflate(R.layout.editscenesingle, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.Text = (TextView) convertView.findViewById(R.id.switchTextView);
			viewHolder.switchTextLinearLayout = (LinearLayout) convertView.findViewById(R.id.switchTextLinearLayout);

		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.id.switchTextLinearLayout);
		}

		viewHolder.Position = position;
		convertView.setTag(R.id.switchTextLinearLayout, viewHolder);
		viewHolder.Text.setText(listText[position]);

		viewHolder.switchTextLinearLayout.setTag(position);

		viewHolder.switchTextLinearLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final int position = (Integer) v.getTag();
				Intent intent = new Intent(context, EditSceneViewActivity.class);
				intent.putExtra("scene_id", position);
				intent.putExtra("Type", "Edit");
				((Activity) context).startActivity(intent);
			}

		});
		return convertView;

	}
}
