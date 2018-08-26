package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class ScheduleActivity extends BaseActivity {
	LinearLayout Masterlayout;
	LinearLayout DynamicSwitch;
	LinearLayout ScheduleToggle;
	RelativeLayout addRelativeLayout;
	String scheduleJSON = "scheduleJSON.cfg";
	String rawScheduleJSON = "";
	String masterJSON = "masterJSON.cfg";
	String rawMasterJSON = "";
	private ScheduleChange ScheduleChangeReceiver;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.scheduleblank);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		
		super.onCreate(savedInstanceState);

		ScheduleChangeReceiver = new ScheduleChange();
		IntentFilter intentSFilter = new IntentFilter("ScheduleChange");
		registerReceiver(ScheduleChangeReceiver, intentSFilter);
	}

	protected void onResume() {
		super.onResume();
		updateView();
	}

	public void updateView() {
		ArrayList<String> id = new ArrayList<String>();
		ArrayList<String> time = new ArrayList<String>();
		ArrayList<String> command = new ArrayList<String>();
		ArrayList<String> status = new ArrayList<String>();

		Masterlayout = (LinearLayout) findViewById(R.id.Masterlayout);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ArrayList<View> custombuttonview = new ArrayList<View>();

		try {
			FileInputStream fis = openFileInput(scheduleJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);

			rawScheduleJSON = bufferedReader.readLine();

			JSONObject objJSON = new JSONObject(rawScheduleJSON);
			JSONArray arrJSON = objJSON.getJSONArray("Schedule");
			for (int i = 0; i < arrJSON.length(); i++) {
				id.add(arrJSON.getJSONObject(i).getString("Id"));
				time.add(arrJSON.getJSONObject(i).getString("TS"));
				command.add(arrJSON.getJSONObject(i).getString("Command"));
				status.add(arrJSON.getJSONObject(i).getString("Status"));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FileInputStream fis = openFileInput(masterJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			rawMasterJSON = bufferedReader.readLine();

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < id.size(); i++) {
			custombuttonview.add(inflater.inflate(R.layout.scheduleswitch, Masterlayout, false));
			Masterlayout.addView(custombuttonview.get(i));
		}

		int scheduleLayout_ID = 0;
		int timeTextView_ID = 0;
		int ampmTextView_ID = 0;
		int roomswitchTextView_ID = 0;
		int scheduleImageViewBackground_ID = 0;
		for (int i = 0; i < id.size(); i++) {
			String cur_schedule_time = time.get(i);
			String cur_hrs = cur_schedule_time.substring(0, 2);
			String cur_min = cur_schedule_time.substring(2, 4);
			String cur_ampm = "";
			String cur_roomswitch_info = command.get(i);
			String cur_status = status.get(i);
			String cur_room = cur_roomswitch_info.substring(1, 3);
			String cur_switch = cur_roomswitch_info.substring(4, 6);
			String cur_switch_status = cur_roomswitch_info.substring(6, 8);

			String cur_roomname = null;
			String cur_switchname = null;
			JSONObject objJSON;
			try {
				objJSON = new JSONObject(rawMasterJSON);
				cur_roomname = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getString("RoomName");
				cur_switchname = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getJSONArray("Switch").getJSONObject(Integer.parseInt(cur_switch)).getString("SwitchName");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (Integer.parseInt(cur_hrs) > 12) {
				cur_hrs = String.format("%02d", Integer.parseInt(cur_hrs) - 12);
				cur_ampm = "PM";
			} else if (Integer.parseInt(cur_hrs) == 12) {
				cur_ampm = "PM";
			} else if (Integer.parseInt(cur_hrs) == 0) {
				cur_hrs = "12";
				cur_ampm = "AM";
			} else {
				cur_ampm = "AM";
			}

			scheduleLayout_ID = 1000 + i;
			timeTextView_ID = 2000 + i;
			ampmTextView_ID = 3000 + i;
			roomswitchTextView_ID = 4000 + i;
			scheduleImageViewBackground_ID = 5000 + i;
			findViewById(R.id.scheduleLayout).setId(scheduleLayout_ID);
			findViewById(R.id.timeTextView).setId(timeTextView_ID);
			findViewById(R.id.ampmTextView).setId(ampmTextView_ID);
			findViewById(R.id.roomswitchTextView).setId(roomswitchTextView_ID);
			findViewById(R.id.scheduleImageViewBackground).setId(scheduleImageViewBackground_ID);
			DynamicSwitch = (LinearLayout) findViewById(scheduleLayout_ID);
			DynamicSwitch.setOnClickListener(getOnClickDoSomething(DynamicSwitch, id.get(i)));

			ScheduleToggle = (LinearLayout) findViewById(scheduleImageViewBackground_ID);
			ScheduleToggle.setOnClickListener(ScheduleToggle(ScheduleToggle, id.get(i)));

			TextView timeTextView = (TextView) findViewById(timeTextView_ID);
			timeTextView.setText(cur_hrs + ":" + cur_min);
			TextView ampmTextView = (TextView) findViewById(ampmTextView_ID);
			ampmTextView.setText(cur_ampm);
			TextView roomswitchTextView = (TextView) findViewById(roomswitchTextView_ID);
			String cur_switch_format_status = null;
			if (cur_switch_status.equals("ON")) {
				cur_switch_format_status = "On";

			} else if (cur_switch_status.equals("OF")) {
				cur_switch_format_status = "Off";
			}
			roomswitchTextView.setText(cur_roomname + " - " + cur_switchname + " - " + cur_switch_format_status);

			if (cur_status.equals("DONE")) {
				int BackgroundColor = getResources().getColor(R.color.foreground_light);
				findViewById(scheduleImageViewBackground_ID).setBackgroundColor(BackgroundColor);
			} else if (cur_status.equals("PEND")) {
				int BackgroundColor = getResources().getColor(R.color.holo_blue);
				findViewById(scheduleImageViewBackground_ID).setBackgroundColor(BackgroundColor);
			}

			addRelativeLayout = (RelativeLayout) findViewById(R.id.addRelativeLayout);
			addRelativeLayout.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					Intent intent = new Intent(getApplicationContext(), AddScheduleActivity.class);
					startActivity(intent);

				}

			});
		}

	}

	public class ScheduleChange extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateView();
		}
	}

	View.OnClickListener getOnClickDoSomething(final LinearLayout buttonLayout, final String id) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SetScheduleActivity.class);
				intent.putExtra("id", id);
				startActivity(intent);
			}
		};
	}

	View.OnClickListener ScheduleToggle(final LinearLayout buttonLayout, final String id) {
		return new View.OnClickListener() {
			public void onClick(View v) {

				String scheduleJSON = "scheduleJSON.cfg";
				String rawScheduleJSON = null;
				try {
					FileInputStream fis = openFileInput(scheduleJSON);
					InputStreamReader isr = new InputStreamReader(fis);
					BufferedReader bufferedReader = new BufferedReader(isr);

					rawScheduleJSON = bufferedReader.readLine();
					JSONObject objJSON = new JSONObject(rawScheduleJSON);
					JSONArray arrJSON = objJSON.getJSONArray("Schedule");
					for (int i = 0; i < arrJSON.length(); i++) {

						if (id.equals(objJSON.getJSONArray("Schedule").getJSONObject(i).getString("Id"))) {

							if (objJSON.getJSONArray("Schedule").getJSONObject(i).getString("Status").equals("DONE")) {
								objJSON.getJSONArray("Schedule").getJSONObject(i).remove("Status");
								objJSON.getJSONArray("Schedule").getJSONObject(i).put("Status", "PEND");
								String ts = objJSON.getJSONArray("Schedule").getJSONObject(i).getString("TS");
								Calendar calendar = Calendar.getInstance();
								calendar.setTimeInMillis(System.currentTimeMillis());
								calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ts.substring(0, 2)));
								calendar.set(Calendar.MINUTE, Integer.parseInt(ts.substring(2, 4)));
								calendar.set(Calendar.SECOND, 0);

								long settime = calendar.getTimeInMillis();
								long curtime = Calendar.getInstance().getTimeInMillis();
								if (curtime > settime) {
									calendar.add(Calendar.DATE, 1);
								}
								// Intent intentservice = new
								// Intent(getApplicationContext(),
								// ScheduleService.class);
								// intentservice.putExtra("Id", id);
								// PendingIntent pendingintent;
								// pendingintent =
								// PendingIntent.getService(getApplicationContext(),
								// (int) Long.parseLong(id), intentservice, 0);
								// pendingintent.cancel();
								// pendingintent =
								// PendingIntent.getService(getApplicationContext(),
								// (int) Long.parseLong(id), intentservice, 0);
								// AlarmManager alarmManager = (AlarmManager)
								// getSystemService(ALARM_SERVICE);
								// alarmManager.set(AlarmManager.RTC_WAKEUP,
								// calendar.getTimeInMillis(), pendingintent);

							} else {
								objJSON.getJSONArray("Schedule").getJSONObject(i).remove("Status");
								objJSON.getJSONArray("Schedule").getJSONObject(i).put("Status", "DONE");
								// Intent intentservice = new
								// Intent(getApplicationContext(),
								// ScheduleService.class);
								// intentservice.putExtra("Id", id);
								// PendingIntent pendingintent;
								// pendingintent =
								// PendingIntent.getService(getApplicationContext(),
								// (int) Long.parseLong(id), intentservice, 0);
								// pendingintent.cancel();
							}
							rawScheduleJSON = objJSON.toString();
							FileOutputStream outputStream;
							try {
								outputStream = openFileOutput(scheduleJSON, Context.MODE_PRIVATE);
								outputStream.write(rawScheduleJSON.getBytes());
								outputStream.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							Intent broadcastIntent = new Intent();
							broadcastIntent.setAction("ScheduleChange");
							sendBroadcast(broadcastIntent);
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				updateView();
			}

		};
	}

}