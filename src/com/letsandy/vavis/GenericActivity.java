package com.letsandy.vavis;

import java.util.ArrayList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GenericActivity extends BaseActivity {
	LinearLayout Masterlayout;
	LinearLayout DynamicSwitch;
	RelativeLayout addRelativeLayout;
	ArrayList<String> HEAD = new ArrayList<String>();
	ArrayList<String> DESC = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		HEAD = (ArrayList<String>) getIntent().getSerializableExtra("HEAD");
		DESC = (ArrayList<String>) getIntent().getSerializableExtra("DESC");
		setContentView(R.layout.genericblank);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);
	}

	protected void onResume() {
		super.onResume();
		updateView();
	}

	public void updateView() {

		Masterlayout = (LinearLayout) findViewById(R.id.Masterlayout);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ArrayList<View> custombuttonview = new ArrayList<View>();
		Masterlayout.removeAllViews();

		for (int i = 0; i < HEAD.size(); i++) {
			custombuttonview.add(inflater.inflate(R.layout.genericswitch, Masterlayout, false));
			Masterlayout.addView(custombuttonview.get(i));
		}

		int genericLayout_ID = 0;
		int headTextView_ID = 0;
		int descTextView_ID = 0;
		for (int i = 0; i < HEAD.size(); i++) {
			String HEAD_TEXT = HEAD.get(i);
			String DESC_TEXT = DESC.get(i);

			genericLayout_ID = 1000 + i;
			headTextView_ID = 2000 + i;
			descTextView_ID = 4000 + i;

			findViewById(R.id.genericLayout).setId(genericLayout_ID);
			findViewById(R.id.headTextView).setId(headTextView_ID);
			findViewById(R.id.descTextView).setId(descTextView_ID);

			TextView headTextView = (TextView) findViewById(headTextView_ID);
			headTextView.setText(HEAD_TEXT);
			TextView descTextView = (TextView) findViewById(descTextView_ID);
			descTextView.setText(DESC_TEXT);

		}

	}

}