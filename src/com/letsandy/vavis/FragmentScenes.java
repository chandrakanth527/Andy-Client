package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.letsandy.vavis.MyPagerAdapter.FragmentLifecycle;
import com.letsandy.vavis.R;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.support.v4.app.Fragment;

@SuppressLint("DefaultLocale")
public class FragmentScenes extends Fragment implements FragmentLifecycle {
	GridView sceneGridView;
	SceneGridAdapter sceneAdapter;

	ArrayList<String> scene_name = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		scene_name.clear();
		try {
			String sceneJSON = "sceneJSON.cfg";
			FileInputStream fis = getActivity().openFileInput(sceneJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawMasterJSON;
			rawMasterJSON = bufferedReader.readLine();
			JSONObject objJSON = new JSONObject(rawMasterJSON);
			JSONArray arrJSON = objJSON.getJSONArray("Scene");

			for (int i = 0; i < arrJSON.length(); i++) {
				scene_name.add(arrJSON.getJSONObject(i).getString("SceneName"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		final View view = inflater.inflate(R.layout.scenegrid, container, false);
		sceneGridView = (GridView) view.findViewById(R.id.sceneGridView);
		sceneAdapter = new SceneGridAdapter(this, scene_name.toArray(new String[scene_name.size()]));
		sceneGridView.setAdapter(sceneAdapter);
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		sceneAdapter.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onResumeFragment() {
		
	}

}
