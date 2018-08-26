package com.letsandy.vavis;

import android.os.Bundle;
import android.view.Window;

public class BlankActivity extends BaseActivity {

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.addscheduleview);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);
	}

}
