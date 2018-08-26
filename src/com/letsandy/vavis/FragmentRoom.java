package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.letsandy.vavis.MyPagerAdapter.FragmentLifecycle;
import com.letsandy.vavis.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v4.app.Fragment;

public class FragmentRoom extends Fragment implements FragmentLifecycle {

	String List_Item_Id;
	LayoutInflater inflater1;
	View current_view;
	ListView roomListView;
	RoomListAdapter roomAdapter;
	JSONObject objJSON;
	String switchStatusJSON = "statusJSON.cfg";
	String masterJSON = "masterJSON.cfg";
	ArrayList<String> room_name = new ArrayList<String>();
	String[] STATUS_DATA = new String[] { "100", "001", "101", "111", "000", "010" };
	ArrayList<String> status_data = new ArrayList<String>();
	updateSwitchView updateSwitchView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		updateSwitchView = new updateSwitchView();
		IntentFilter intentUpdateSwitchView = new IntentFilter("com.letsandy.updateSwitchView");
		getActivity().registerReceiver(updateSwitchView, intentUpdateSwitchView);
		View view = inflater.inflate(R.layout.roomlist, container, false);
		roomListView = (ListView) view.findViewById(R.id.roomListView);

		room_name.clear();
		try {
			FileInputStream fis = getActivity().openFileInput(masterJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawMasterJSON;
			rawMasterJSON = bufferedReader.readLine();

			objJSON = new JSONObject(rawMasterJSON);
			JSONArray arrJSON = objJSON.getJSONArray("Room");
			for (int i = 0; i < arrJSON.length(); i++) {
				room_name.add(arrJSON.getJSONObject(i).getString("RoomName"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FileInputStream fis = getActivity().openFileInput(switchStatusJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawSwitchStatusJSON;
			rawSwitchStatusJSON = bufferedReader.readLine();

			JSONObject objStatusJSON = new JSONObject(rawSwitchStatusJSON);
			JSONArray arrStatusJSON = objStatusJSON.getJSONArray("Room");
			for (int i = 0; i < arrStatusJSON.length(); i++) {
				JSONArray arrSwitchStatusJSON = arrStatusJSON.getJSONObject(i).getJSONArray("Switch");
				String switchStatus = "0";
				String fanStatus = "0";
				String curtainStatus = "0";
				for (int j = 0; j < arrSwitchStatusJSON.length(); j++) {
					String tempType = objJSON.getJSONArray("Room").getJSONObject(i).getJSONArray("Switch").getJSONObject(j).getString("Type");
					// String tempType = "Curtain";
					String tempStatus = arrSwitchStatusJSON.getJSONObject(j).getString("SwitchStatus");
					if (tempStatus.equals("ON")) {
						if ((tempType.equals("Normal") && switchStatus.equals("0")) || (tempType.equals("Dimmer") && switchStatus.equals("0"))) {
							switchStatus = "1";
						} else if (tempType.equals("Fan") && fanStatus.equals("0")) {
							fanStatus = "1";
						} else if (tempType.equals("Curtain") && curtainStatus.equals("0")) {
							curtainStatus = "1";
						}
					}

				}
				status_data.add(switchStatus + fanStatus + curtainStatus);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		roomAdapter = new RoomListAdapter(this, room_name.toArray(new String[room_name.size()]), status_data.toArray(new String[status_data.size()]));
		roomListView.setAdapter(roomAdapter);

		return view;
	}

	public void dataChange() {
		roomAdapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		roomAdapter.onActivityResult(requestCode, resultCode, data);
	}

	public class updateSwitchView extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			status_data.clear();
			try {
				FileInputStream fis = getActivity().openFileInput(switchStatusJSON);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(isr);
				String rawSwitchStatusJSON;
				rawSwitchStatusJSON = bufferedReader.readLine();

				JSONObject objStatusJSON = new JSONObject(rawSwitchStatusJSON);
				JSONArray arrStatusJSON = objStatusJSON.getJSONArray("Room");
				for (int i = 0; i < arrStatusJSON.length(); i++) {
					JSONArray arrSwitchStatusJSON = arrStatusJSON.getJSONObject(i).getJSONArray("Switch");
					String switchStatus = "0";
					String fanStatus = "0";
					String curtainStatus = "0";
					for (int j = 0; j < arrSwitchStatusJSON.length(); j++) {
						String tempType = objJSON.getJSONArray("Room").getJSONObject(i).getJSONArray("Switch").getJSONObject(j).getString("Type");
						// String tempType = "Curtain";
						String tempStatus = arrSwitchStatusJSON.getJSONObject(j).getString("SwitchStatus");
						if (tempStatus.equals("ON")) {
							if ((tempType.equals("Normal") && switchStatus.equals("0")) || (tempType.equals("Dimmer") && switchStatus.equals("0"))) {
								switchStatus = "1";
							} else if (tempType.equals("Fan") && fanStatus.equals("0")) {
								fanStatus = "1";
							} else if (tempType.equals("Curtain") && curtainStatus.equals("0")) {
								curtainStatus = "1";
							}
						}

					}
					status_data.add(switchStatus + fanStatus + curtainStatus);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			roomAdapter.updateStatus(status_data.toArray(new String[status_data.size()]));
		}

	}

	@Override
	public void onResumeFragment() {
	}
}
