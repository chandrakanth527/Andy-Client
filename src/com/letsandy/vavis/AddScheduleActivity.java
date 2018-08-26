package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class AddScheduleActivity extends BaseActivity {
	LinearLayout Masterlayout;
	LinearLayout DynamicSwitch;
	ImageView hrsarrow1;
	ImageView hrsarrow2;
	ImageView minarrow1;
	ImageView minarrow2;
	ImageView ampmarrow1;
	ImageView ampmarrow2;
	EditText hrsTextView;
	EditText minTextView;
	TextView ampmTextView;
	TextView roomTextView;
	TextView switchTextView;
	RelativeLayout roomLayout;
	RelativeLayout switchLayout;
	RelativeLayout onRelativeLayout;
	RelativeLayout offRelativeLayout;
	RelativeLayout deleteRelativeLayout;
	RelativeLayout saveRelativeLayout;
	ImageView onImageView;
	ImageView offImageView;

	String time;
	String command;
	String status;
	// String id;
	String scheduleJSON = "scheduleJSON.cfg";
	String rawScheduleJSON = null;
	String masterJSON = "masterJSON.cfg";
	String rawMasterJSON = null;
	String cur_room;
	String cur_switch;
	String cur_switch_status;
	String cur_roomname;
	String cur_switchname;
	String cur_hrs;
	String cur_min;
	String cur_ampm;
	int resID1;
	int resID2;
	PendingIntent pendingintent;

	// int curSchedulePos;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.addschedule);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		super.onCreate(savedInstanceState);
		try {
			FileInputStream fis = openFileInput(scheduleJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);

			rawScheduleJSON = bufferedReader.readLine();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Calendar c = Calendar.getInstance();
		cur_roomname = "";
		cur_switchname = "";
		cur_hrs = String.format("%02d", c.get(Calendar.HOUR_OF_DAY));
		cur_min = String.format("%02d", c.get(Calendar.MINUTE));
		// cur_ampm = "";

		// cur_room = command.substring(1, 3);
		// cur_switch = command.substring(4, 6);
		// cur_switch_status = command.substring(6, 8);

		cur_room = "xx";
		cur_switch = "xx";
		cur_switch_status = "ON";
		String cur_roomname = null;
		String cur_switchname = null;

		try {
			FileInputStream fis = openFileInput(masterJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			rawMasterJSON = bufferedReader.readLine();

		} catch (Exception e) {
			e.printStackTrace();
		}

		cur_roomname = "Select Room";
		cur_switchname = "Select Switch";

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

		hrsTextView = (EditText) findViewById(R.id.hrsTextView);
		hrsTextView.setText(cur_hrs);
		minTextView = (EditText) findViewById(R.id.minTextView);
		minTextView.setText(cur_min);
		ampmTextView = (TextView) findViewById(R.id.ampmTextView);
		ampmTextView.setText(cur_ampm);

		hrsTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (hrsTextView.getText().toString().equals("")) {
						Toast.makeText(getApplicationContext(), "Invalid Hours", Toast.LENGTH_SHORT).show();
					} else if (minTextView.getText().toString().equals("")) {
						Toast.makeText(getApplicationContext(), "Invalid Minutes", Toast.LENGTH_SHORT).show();
					} else if (Integer.parseInt(hrsTextView.getText().toString()) > 12) {
						hrsTextView.setText("12");
					}
				}
			}
		});

		minTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (hrsTextView.getText().toString().equals("")) {
						Toast.makeText(getApplicationContext(), "Invalid Hours", Toast.LENGTH_SHORT).show();
					} else if (minTextView.getText().toString().equals("")) {
						Toast.makeText(getApplicationContext(), "Invalid Minutes", Toast.LENGTH_SHORT).show();
					} else if (Integer.parseInt(minTextView.getText().toString()) > 59) {
						minTextView.setText("00");
					}
				}
			}
		});

		hrsarrow1 = (ImageView) findViewById(R.id.hrsarrow1);
		hrsarrow1.setClickable(true);
		hrsarrow1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (Integer.parseInt(hrsTextView.getText().toString()) > 12 || Integer.parseInt(hrsTextView.getText().toString()) < 1) {
					hrsTextView.setText("12");
				}
				int set_hrs = Integer.parseInt(hrsTextView.getText().toString());

				if (set_hrs == 12) {
					set_hrs = 1;
				} else {
					set_hrs = set_hrs + 1;
				}
				hrsTextView.setText(String.format("%02d", set_hrs));
				cur_hrs = String.format("%02d", set_hrs);
			}
		});

		hrsarrow2 = (ImageView) findViewById(R.id.hrsarrow2);
		hrsarrow2.setClickable(true);
		hrsarrow2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (Integer.parseInt(hrsTextView.getText().toString()) > 12 || Integer.parseInt(hrsTextView.getText().toString()) < 1) {
					hrsTextView.setText("01");
				}
				int set_hrs = Integer.parseInt(hrsTextView.getText().toString());

				if (set_hrs == 1) {
					set_hrs = 12;
				} else {
					set_hrs = set_hrs - 1;
				}
				hrsTextView.setText(String.format("%02d", set_hrs));
				cur_hrs = String.format("%02d", set_hrs);
			}
		});

		minarrow1 = (ImageView) findViewById(R.id.minarrow1);
		minarrow1.setClickable(true);
		minarrow1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (Integer.parseInt(minTextView.getText().toString()) > 59 || Integer.parseInt(minTextView.getText().toString()) < 0) {
					minTextView.setText("59");
				}
				int set_min = Integer.parseInt(minTextView.getText().toString());

				if (set_min == 59) {
					set_min = 0;
				} else {
					set_min = set_min + 1;
				}
				minTextView.setText(String.format("%02d", set_min));
				cur_min = String.format("%02d", set_min);
			}
		});

		minarrow2 = (ImageView) findViewById(R.id.minarrow2);
		minarrow2.setClickable(true);
		minarrow2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (Integer.parseInt(minTextView.getText().toString()) > 59 || Integer.parseInt(minTextView.getText().toString()) < 0) {
					minTextView.setText("00");
				}
				int set_min = Integer.parseInt(minTextView.getText().toString());

				if (set_min == 0) {
					set_min = 59;
				} else {
					set_min = set_min - 1;
				}
				minTextView.setText(String.format("%02d", set_min));
				cur_min = String.format("%02d", set_min);
			}
		});

		ampmarrow1 = (ImageView) findViewById(R.id.ampmarrow1);
		ampmarrow1.setClickable(true);
		ampmarrow1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (ampmTextView.getText().equals("AM")) {
					ampmTextView.setText("PM");
					cur_ampm = "PM";
				} else {
					ampmTextView.setText("AM");
					cur_ampm = "AM";
				}

			}
		});

		ampmarrow2 = (ImageView) findViewById(R.id.ampmarrow2);
		ampmarrow2.setClickable(true);
		ampmarrow2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (ampmTextView.getText().equals("AM")) {
					ampmTextView.setText("PM");
					cur_ampm = "PM";
				} else {
					ampmTextView.setText("AM");
					cur_ampm = "AM";
				}
			}
		});

		roomTextView = (TextView) findViewById(R.id.roomTextView);
		roomTextView.setText(cur_roomname);
		switchTextView = (TextView) findViewById(R.id.switchTextView);
		switchTextView.setText(cur_switchname);

		roomLayout = (RelativeLayout) findViewById(R.id.roomLayout);
		roomLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SelectRoomSwitchActivity.class);
				intent.putExtra("type", "room");
				intent.putExtra("room_id", cur_room);
				intent.putExtra("switch_id", cur_switch);
				startActivityForResult(intent, 2);
			}
		});

		switchLayout = (RelativeLayout) findViewById(R.id.switchLayout);
		switchLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (cur_room.equals("xx")) {
					Toast.makeText(getApplicationContext(), "Select Room", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(getApplicationContext(), SelectRoomSwitchActivity.class);
					intent.putExtra("type", "switch");
					intent.putExtra("room_id", cur_room);
					intent.putExtra("switch_id", cur_switch);
					startActivityForResult(intent, 2);
				}
			}
		});

		resID1 = getResources().getIdentifier("ic_radio1", "drawable", this.getPackageName());
		resID2 = getResources().getIdentifier("ic_radio2", "drawable", this.getPackageName());
		if (cur_switch_status.equals("ON")) {
			onImageView = (ImageView) findViewById(R.id.onImageView);
			onImageView.setImageResource(resID1);
			offImageView = (ImageView) findViewById(R.id.offImageView);
			offImageView.setImageResource(resID2);

		} else {
			onImageView = (ImageView) findViewById(R.id.onImageView);
			onImageView.setImageResource(resID2);
			offImageView = (ImageView) findViewById(R.id.offImageView);
			offImageView.setImageResource(resID1);

		}

		onRelativeLayout = (RelativeLayout) findViewById(R.id.onRelativeLayout);
		onRelativeLayout.setClickable(true);
		onRelativeLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cur_switch_status = "ON";
				onImageView.setImageResource(resID1);
				offImageView.setImageResource(resID2);

			}
		});

		offRelativeLayout = (RelativeLayout) findViewById(R.id.offRelativeLayout);
		offRelativeLayout.setClickable(true);
		offRelativeLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cur_switch_status = "OF";
				onImageView.setImageResource(resID2);
				offImageView.setImageResource(resID1);

			}
		});

		deleteRelativeLayout = (RelativeLayout) findViewById(R.id.deleteRelativeLayout);
		deleteRelativeLayout.setClickable(true);
		deleteRelativeLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();

			}
		});

		saveRelativeLayout = (RelativeLayout) findViewById(R.id.saveRelativeLayout);
		saveRelativeLayout.setClickable(true);
		saveRelativeLayout.setOnClickListener(new View.OnClickListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@SuppressLint("SimpleDateFormat")
			final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

			public void onClick(View v) {
				if (hrsTextView.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "Invalid Hours", Toast.LENGTH_SHORT).show();
				} else if (minTextView.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "Invalid Minutes", Toast.LENGTH_SHORT).show();
				} else if (Integer.parseInt(minTextView.getText().toString()) > 59 || Integer.parseInt(minTextView.getText().toString()) < 0) {
					Toast.makeText(getApplicationContext(), "Invalid Minutes", Toast.LENGTH_SHORT).show();
				} else if (Integer.parseInt(hrsTextView.getText().toString()) > 12 || Integer.parseInt(hrsTextView.getText().toString()) < 1) {
					Toast.makeText(getApplicationContext(), "Invalid Hours", Toast.LENGTH_SHORT).show();
				} else if (cur_room.equals("xx")) {
					Toast.makeText(getApplicationContext(), "Select Room", Toast.LENGTH_SHORT).show();
				} else if (cur_switch.equals("xx")) {
					Toast.makeText(getApplicationContext(), "Select Switch", Toast.LENGTH_SHORT).show();
				} else {
					JSONObject objJSON;
					JSONObject newJSON = new JSONObject();
					String command_new;
					String ts_new = null;
					try {
						objJSON = new JSONObject(rawScheduleJSON);
						command_new = "R" + String.format("%02d", Integer.parseInt(cur_room)) + "S" + String.format("%02d", Integer.parseInt(cur_switch)) + cur_switch_status;
						SimpleDateFormat displayFormat = new SimpleDateFormat("HHmm");
						SimpleDateFormat parseFormat = new SimpleDateFormat("hhmma");
						ts_new = displayFormat.format(parseFormat.parse(cur_hrs + cur_min + cur_ampm));
						long time = System.currentTimeMillis();
						newJSON.put("Id", Long.toString(time));
						newJSON.put("TS", ts_new);
						newJSON.put("Command", command_new);
						newJSON.put("Status", "PEND");
						objJSON.getJSONArray("Schedule").put(newJSON);

						rawScheduleJSON = objJSON.toString();

						FileOutputStream outputStream;
						try {
							outputStream = openFileOutput(scheduleJSON, Context.MODE_PRIVATE);
							outputStream.write(rawScheduleJSON.getBytes());
							outputStream.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(System.currentTimeMillis());
						calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ts_new.substring(0, 2)));
						calendar.set(Calendar.MINUTE, Integer.parseInt(ts_new.substring(2, 4)));
						calendar.set(Calendar.SECOND, 0);

						long settime = calendar.getTimeInMillis();
						long curtime = Calendar.getInstance().getTimeInMillis();
						if (curtime > settime) {
							calendar.add(Calendar.DATE, 1);
						}
						// Intent intentservice = new
						// Intent(getApplicationContext(),
						// ScheduleService.class);
						// intentservice.putExtra("Id", Long.toString(time));
						// pendingintent =
						// PendingIntent.getService(getApplicationContext(),
						// (int) time, intentservice, 0);
						// pendingintent.cancel();
						// pendingintent =
						// PendingIntent.getService(getApplicationContext(),
						// (int) time, intentservice, 0);
						// alarmManager.set(AlarmManager.RTC_WAKEUP,
						// calendar.getTimeInMillis(), pendingintent);

					} catch (JSONException e) {
						e.printStackTrace();
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
					Toast.makeText(getApplicationContext(), "Schedule Added", Toast.LENGTH_SHORT).show();

					finish();
				}

			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 2) {
			if (data.getStringExtra("type").equals("room")) {
				String return_room_id = data.getStringExtra("roomswitch_id");
				if (!return_room_id.equals(cur_room)) {
					cur_room = return_room_id;
					JSONObject objJSON;
					try {
						objJSON = new JSONObject(rawMasterJSON);
						cur_roomname = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getString("RoomName");

					} catch (JSONException e) {
						e.printStackTrace();
					}
					roomTextView = (TextView) findViewById(R.id.roomTextView);
					roomTextView.setText(cur_roomname);

					cur_switchname = "Select Switch";
					cur_switch = "xx";
					switchTextView = (TextView) findViewById(R.id.switchTextView);
					switchTextView.setText("Select Switch");
				}

			} else if (data.getStringExtra("type").equals("switch")) {
				String return_switch_id = data.getStringExtra("roomswitch_id");
				if (!return_switch_id.equals(cur_switch)) {
					cur_switch = return_switch_id;
					JSONObject objJSON;
					try {
						objJSON = new JSONObject(rawMasterJSON);
						cur_switchname = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getJSONArray("Switch").getJSONObject(Integer.parseInt(cur_switch)).getString("SwitchName");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					cur_switchname = "Select Switch";
					cur_switch = "xx";
				}
				switchTextView = (TextView) findViewById(R.id.switchTextView);
				switchTextView.setText(cur_switchname);

			}

		}

	}

}