package com.letsandy.vavis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("InflateParams")
public class CustomIconGridAdapter extends BaseAdapter {
	private Context context;
	private final String[] gridIcons;
	ViewHolder viewHolder;

	public CustomIconGridAdapter(Context context, String[] gridIcons) {
		this.context = context;
		this.gridIcons = gridIcons;
	}

	@Override
	public int getCount() {
		return gridIcons.length;
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
			convertView = inflater.inflate(R.layout.icongridsingle, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.Image = (ImageView) convertView.findViewById(R.id.grid_image);

		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.id.grid_image);
		}

		viewHolder.Position = position;
		convertView.setTag(R.id.grid_image, viewHolder);

		String Icon = gridIcons[position];
		int resID = context.getResources().getIdentifier(Icon, "drawable", context.getPackageName());
		viewHolder.Image.setImageResource(resID);

		return convertView;

	}

}
