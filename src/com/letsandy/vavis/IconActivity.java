package com.letsandy.vavis;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.content.Intent;

public class IconActivity extends BaseActivity {
	GridView iconGridView;
	final String[] GRID_DATA = new String[] { "ic_icon1", "ic_icon2", "ic_icon3", "ic_icon4", "ic_icon5", "ic_icon6", "ic_icon7", "ic_icon8", "ic_icon9", "ic_icon10", "ic_icon11", "ic_icon12", "ic_icon13", "ic_icon14", "ic_icon15", "ic_icon16", "ic_icon17", "ic_icon18", "ic_icon19", "ic_icon20", "ic_icon21", "ic_icon22", "ic_icon23", "ic_icon24", "ic_icon25", "ic_icon26", "ic_icon27", "ic_icon28", "ic_icon29", "ic_icon30", "ic_icon31", "ic_icon32", "ic_icon33", "ic_icon34", "ic_icon35", "ic_icon36" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.icongrid);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		final String ListPosition = intent.getStringExtra("ListPosition");
		iconGridView = (GridView) findViewById(R.id.iconGridView);
		iconGridView.setAdapter(new CustomIconGridAdapter(this, GRID_DATA));
		iconGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("icon", GRID_DATA[position]);
				intent.putExtra("ListPosition", ListPosition);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

	}

}
