package com.letsandy.vavis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("InflateParams")
public class CustomRoomFullGridAdapter extends BaseAdapter {
	private Context context;
	private final String[] gridValues;
	private final String[] gridIcons;
	ViewHolder viewHolder;

	public CustomRoomFullGridAdapter(Context context, String[] gridValues, String[] gridIcons) {
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

	// Number of times getView method call depends upon gridValues.length

	@SuppressLint("ViewTag") public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.roomfullgridsingle, null);
			
			viewHolder = new ViewHolder();
			viewHolder.Text = (TextView) convertView.findViewById(R.id.grid_text);
			viewHolder.Image = (ImageView) convertView.findViewById(R.id.grid_image);

		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.id.grid_text);
		}
		viewHolder.Position = position;
		convertView.setTag(R.id.grid_text, viewHolder);
		viewHolder.Text.setText(gridValues[position]);

		String Icon = gridIcons[position];
		int resID = context.getResources().getIdentifier(Icon, "drawable", context.getPackageName());
		viewHolder.Image.setImageResource(resID);

		return convertView;

	}

}
