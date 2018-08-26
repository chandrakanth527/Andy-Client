package com.letsandy.vavis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("InflateParams") public class CustomRemoteGridAdapter extends BaseAdapter {
	private Context context;
	private final String[] gridValues;
	private final String[] gridIcons;

	public CustomRemoteGridAdapter(Context context, String[] gridValues, String[] gridIcons) {
		this.context = context;
		this.gridValues = gridValues;
		this.gridIcons = gridIcons;
	}

	@Override
	public int getCount() {
		return gridValues.length;
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
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View gridView;
		if (convertView == null) {
			gridView = new View(context);
			gridView = inflater.inflate(R.layout.remotegridsingle, null);
			TextView textView = (TextView) gridView.findViewById(R.id.grid_text);
			textView.setText(gridValues[position]);
			ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_image);
			String sceneIcon = gridIcons[position];
			int resID = context.getResources().getIdentifier(sceneIcon, "drawable", context.getPackageName());
			imageView.setImageResource(resID);
		} else {
			gridView = (View) convertView;
		}

		return gridView;

	}

}
