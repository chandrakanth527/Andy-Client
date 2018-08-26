package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

public class SettingsActivity extends BaseActivity {
	ListView customListView;
	ArrayList<String> setting_name = new ArrayList<String>();
	ArrayList<String> setting_icon = new ArrayList<String>();

	CustomSettingsAdapter customSettingsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.customlist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);

		setting_name.clear();
		setting_icon.clear();

		String configJSON = "configJSON.cfg";
		String rawConfigJSON;
		JSONObject objStatusJSON;
		try {
			FileInputStream fis = openFileInput(configJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			rawConfigJSON = bufferedReader.readLine();
			objStatusJSON = new JSONObject(rawConfigJSON).getJSONObject("Options");
			if (objStatusJSON.has("VOICE") && objStatusJSON.getString("VOICE").equals("yes")) {
				setting_name.add("Add Voice Commands");
				setting_icon.add("ic_voice");
			}
			if (objStatusJSON.has("VAVIS") && objStatusJSON.getString("VAVIS").equals("yes")) {
				setting_name.add("Edit Switches");
				setting_icon.add("ic_plug");
			}
			if (objStatusJSON.has("SCHEDULE") && objStatusJSON.getString("SCHEDULE").equals("yes")) {

			}
			if (objStatusJSON.has("SCENE") && objStatusJSON.getString("SCENE").equals("yes")) {

			}
			if (objStatusJSON.has("CCTV") && objStatusJSON.getString("CCTV").equals("yes")) {

			}
			if (objStatusJSON.has("ZMOTE") && objStatusJSON.getString("ZMOTE").equals("yes")) {
				setting_name.add("Edit Remotes");
				setting_icon.add("ic_remote");
				setting_name.add("Infrared Controller Info");
				setting_icon.add("ic_remote");
			}
			if (objStatusJSON.has("SECURITY") && objStatusJSON.getString("SECURITY").equals("yes")) {

			}

			setting_name.add("Pull Configuration");
			setting_icon.add("ic_arrow2");

			setting_name.add("Push Configuration");
			setting_icon.add("ic_arrow1");

			setting_name.add("Reset Application");
			setting_icon.add("ic_refresh");

			setting_name.add("Version V1.2");
			setting_icon.add("ic_version");

		} catch (Exception e) {
			e.printStackTrace();
		}

		customListView = (ListView) findViewById(R.id.customListView);
		customSettingsAdapter = new CustomSettingsAdapter(this, setting_name.toArray(new String[setting_name.size()]), setting_icon.toArray(new String[setting_icon.size()]));
		customListView.setAdapter(customSettingsAdapter);

	}

}
