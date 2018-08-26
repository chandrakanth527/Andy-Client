package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONObject;
import com.letsandy.vavis.R;
import com.letsandy.vavis.MyPagerAdapter.FragmentLifecycle;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.support.v4.app.Fragment;

@SuppressLint("DefaultLocale")
public class FragmentServices extends Fragment implements FragmentLifecycle {
	GridView serviceGridView;
	ArrayList<String> service_name = new ArrayList<String>();
	ArrayList<String> service_icon = new ArrayList<String>();

	final String[] GRID_DATA = new String[] { "Switch", "Schedule", "Scene", "CCTV", "Voice", "Remotes", "Security", "Settings" };
	final String[] GRID_ICON = new String[] { "ic_switch", "ic_schedule", "ic_scene", "ic_cctv", "ic_voice", "ic_remote", "ic_security", "ic_settings" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		String configJSON = "configJSON.cfg";
		String rawConfigJSON;
		JSONObject objStatusJSON;
		try {
			FileInputStream fis = getActivity().openFileInput(configJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			rawConfigJSON = bufferedReader.readLine();
			objStatusJSON = new JSONObject(rawConfigJSON).getJSONObject("Options");
			service_name.clear();
			service_icon.clear();
			if (objStatusJSON.has("VAVIS") && objStatusJSON.getString("VAVIS").equals("yes")) {
				service_name.add("Switch");
				service_icon.add("ic_switch");
			}
			if (objStatusJSON.has("SCHEDULE") && objStatusJSON.getString("SCHEDULE").equals("yes")) {
				service_name.add("Schedule");
				service_icon.add("ic_schedule");
			}
			if (objStatusJSON.has("SCENE") && objStatusJSON.getString("SCENE").equals("yes")) {
				service_name.add("Scene");
				service_icon.add("ic_scene");
			}
			if (objStatusJSON.has("CCTV") && objStatusJSON.getString("CCTV").equals("yes")) {
				service_name.add("CCTV");
				service_icon.add("ic_cctv");
			}
			if (objStatusJSON.has("VOICE") && objStatusJSON.getString("VOICE").equals("yes")) {
				service_name.add("Voice");
				service_icon.add("ic_voice");
			}
			if (objStatusJSON.has("ZMOTE") && objStatusJSON.getString("ZMOTE").equals("yes")) {
				service_name.add("Remotes");
				service_icon.add("ic_remote");
			}
			if (objStatusJSON.has("SECURITY") && objStatusJSON.getString("SECURITY").equals("yes")) {
				service_name.add("Security");
				service_icon.add("ic_security");
			}
			service_name.add("Settings");
			service_icon.add("ic_settings");
		} catch (Exception e) {
			e.printStackTrace();
		}

		final View view = inflater.inflate(R.layout.servicegrid, container, false);
		serviceGridView = (GridView) view.findViewById(R.id.serviceGridView);
		serviceGridView.setAdapter(new ServiceGridAdapter(this, service_name.toArray(new String[service_name.size()]), service_icon.toArray(new String[service_icon.size()])));
		return view;
	}

	@Override
	public void onResumeFragment() {
	}

}
