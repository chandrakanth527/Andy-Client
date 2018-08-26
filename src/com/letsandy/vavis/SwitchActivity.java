package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SwitchActivity extends BaseActivity implements OnSeekBarChangeListener {
	LinearLayout Masterlayout;
	RelativeLayout DynamicSwitch;
	LinearLayout DynamicStopSwitch;
	LinearLayout DynamicOnSwitch;
	LinearLayout DynamicOffSwitch;

	SeekBar DimmerSeekBar;
	String room_id;
	String type;
	String room_name;
	clientConnectedMQTT clientConnectedMQTT;
	connectionLostMQTT connectionLostMQTT;
	String masterJSON = "masterJSON.cfg";
	String switchStatusJSON = "statusJSON.cfg";
	String switchconfigfile = "switch.cfg";
	String configfile = "config.cfg";
	JSONArray arrStatusJSON;
	updateSwitchView updateSwitchView;
	fullUpdateSwitchView fullUpdateSwitchView;
	int curProgress = 0;

	ArrayList<String> switch_id = new ArrayList<String>();
	ArrayList<String> switch_name = new ArrayList<String>();
	ArrayList<String> switch_icon = new ArrayList<String>();
	ArrayList<String> switch_type = new ArrayList<String>();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.switchblank);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		
		super.onCreate(savedInstanceState);
		
		updateSwitchView = new updateSwitchView();
		IntentFilter intentUpdateSwitchView = new IntentFilter("com.letsandy.updateSwitchView");
		registerReceiver(updateSwitchView, intentUpdateSwitchView);

		fullUpdateSwitchView = new fullUpdateSwitchView();
		IntentFilter intentFullUpdateSwitchView = new IntentFilter("com.letsandy.fullUpdateSwitchView");
		registerReceiver(fullUpdateSwitchView, intentFullUpdateSwitchView);

		Intent intent = getIntent();
		room_id = intent.getStringExtra("room_id");
		type = intent.getStringExtra("type");

		updateView();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		try {
			unregisterReceiver(updateSwitchView);
		} catch (final Exception exception) {

		}
		try {
			unregisterReceiver(fullUpdateSwitchView);
		} catch (final Exception exception) {

		}
		try {
			unregisterReceiver(clientConnectedMQTT);
		} catch (final Exception exception) {

		}
		try {
			unregisterReceiver(connectionLostMQTT);
		} catch (final Exception exception) {

		}
		super.onDestroy();
	}

	public void updateView() {

		ImageView room = (ImageView) findViewById(R.id.roomImage);
		String pathName = Environment.getExternalStorageDirectory() + File.separator + "vavis" + File.separator + "Room" + room_id + ".jpg";
		File file = new File(pathName);
		SharedPreferences prefs = getSharedPreferences("RoomUnique", 0);
		String RoomUnique = prefs.getString("Room" + room_id, "123");
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int height = metrics.heightPixels;
		int width = metrics.widthPixels;

		final ImageView switchOffImage = (ImageView) findViewById(R.id.switchCompleteOff);
		if (type.equals("All")) {
			switchOffImage.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					ObjectAnimator colorFade = ObjectAnimator.ofObject(switchOffImage, "colorFilter", new ArgbEvaluator(), 0xff00a6d6, 0x00000000);
					colorFade.setDuration(700);
					colorFade.start();
					String Room = "R" + String.format("%02d", Integer.parseInt(room_id));
					messagePublish("request/switchAllOff", Room + "OF");
				}
			});
		} else {
			switchOffImage.setVisibility(View.GONE);
		}

		if (file.exists()) {
			Glide.with(getApplicationContext()).load(file).diskCacheStrategy(DiskCacheStrategy.NONE).dontAnimate().dontTransform().override(width, height).signature(new StringSignature(RoomUnique)).into(room);
		} else {
			Glide.with(getApplicationContext()).load(R.drawable.home).diskCacheStrategy(DiskCacheStrategy.NONE).dontAnimate().dontTransform().override(width, height).signature(new StringSignature(RoomUnique)).into(room);
		}
		Masterlayout = (LinearLayout) findViewById(R.id.Masterlayout);
		LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ArrayList<View> custombuttonview = new ArrayList<View>();

		try {
			FileInputStream fis = openFileInput(masterJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawMasterJSON;
			rawMasterJSON = bufferedReader.readLine();

			JSONObject objJSON = new JSONObject(rawMasterJSON);
			JSONArray arrJSON = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(room_id)).getJSONArray("Switch");
			room_name = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(room_id)).getString("RoomName");
			for (int i = 0; i < arrJSON.length(); i++) {
				switch_id.add(Integer.toString(i));
				switch_name.add(arrJSON.getJSONObject(i).getString("SwitchName"));
				switch_icon.add(arrJSON.getJSONObject(i).getString("SwitchIcon"));
				switch_type.add(arrJSON.getJSONObject(i).getString("Type"));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FileInputStream fis = openFileInput(switchStatusJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawSwitchStatusJSON;
			rawSwitchStatusJSON = bufferedReader.readLine();

			JSONObject objStatusJSON = new JSONObject(rawSwitchStatusJSON);
			arrStatusJSON = objStatusJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(room_id)).getJSONArray("Switch");

		} catch (Exception e) {
			e.printStackTrace();
		}

		TextView roomNameTextView = (TextView) findViewById(R.id.roomName);
		roomNameTextView.setText(room_name);
		int switchLayout_ID = 0;
		int switchTextView_ID = 0;
		int switchImageView_ID = 0;
		int switchImageViewBackground_ID = 0;
		int dimmerSeekBar_ID = 0;
		int dimmerIntensity_ID = 0;
		int curtainStopLayout_ID = 0;
		int switchImageViewToggle_ID = 0;
		int curtainOnLayout_ID = 0;
		int curtainOffLayout_ID = 0;

		for (int i = 0; i < switch_id.size(); i++) {
			@SuppressWarnings("unused")
			String cur_switch_id = switch_id.get(i);
			String cur_switch_name = switch_name.get(i);
			String cur_switch_icon = switch_icon.get(i);
			String cur_switch_type = switch_type.get(i);

			if (cur_switch_type.equals("Dimmer") || cur_switch_type.equals("Fan")) {
				custombuttonview.add(inflater.inflate(R.layout.dimmerswitch, Masterlayout, false));
			} else if (cur_switch_type.equals("Normal") || cur_switch_type.equals("Bell")) {
				custombuttonview.add(inflater.inflate(R.layout.normalswitch, Masterlayout, false));
			} else if (cur_switch_type.equals("Curtain")) {
				custombuttonview.add(inflater.inflate(R.layout.curtainswitch, Masterlayout, false));
			} else if (cur_switch_type.equals("Scene")) {
				custombuttonview.add(inflater.inflate(R.layout.sceneswitch, Masterlayout, false));
			}

			Masterlayout.addView(custombuttonview.get(i));

			switchLayout_ID = 1000 + i;
			switchTextView_ID = 2000 + i;
			switchImageView_ID = 3000 + i;
			switchImageViewBackground_ID = 4000 + i;
			dimmerSeekBar_ID = 5000 + i;
			dimmerIntensity_ID = 6000 + i;
			switchImageViewToggle_ID = 8000 + i;
			curtainStopLayout_ID = 9000 + i;
			curtainOnLayout_ID = 10000 + i;
			curtainOffLayout_ID = 11000 + i;
			findViewById(R.id.switchLayout).setId(switchLayout_ID);
			findViewById(R.id.switchTextView).setId(switchTextView_ID);
			findViewById(R.id.switchImageView).setId(switchImageView_ID);

			TextView switchTextView = (TextView) findViewById(switchTextView_ID);
			switchTextView.setText(cur_switch_name);
			int resID = getResources().getIdentifier(cur_switch_icon, "drawable", this.getPackageName());
			ImageView room_image = (ImageView) findViewById(switchImageView_ID);
			room_image.setImageResource(resID);
			if (cur_switch_type.equals("Normal")) {
				if (!type.equals("All") && !type.equals("Normal")) {
					custombuttonview.get(i).setVisibility(View.GONE);
				}
				findViewById(R.id.switchImageViewBackground).setId(switchImageViewBackground_ID);
				findViewById(R.id.switchImageViewToggle).setId(switchImageViewToggle_ID);
				DynamicSwitch = (RelativeLayout) findViewById(switchLayout_ID);
				DynamicSwitch.setOnClickListener(getOnClickDoSomething(i));
				String curSwitchStatus = null;
				try {
					curSwitchStatus = arrStatusJSON.getJSONObject(i).getString("SwitchStatus");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (curSwitchStatus.equals("ON")) {
					switch_on(switchImageViewBackground_ID);
				} else if (curSwitchStatus.equals("OF")) {
					switch_off(switchImageViewBackground_ID);
				}

			}
			if (cur_switch_type.equals("Scene")) {
				if (!type.equals("All") && !type.equals("Scene")) {
					custombuttonview.get(i).setVisibility(View.GONE);
				}

				DynamicSwitch = (RelativeLayout) findViewById(switchLayout_ID);
				DynamicSwitch.setOnClickListener(getOnClickDoSomething(i));
			}
			if (cur_switch_type.equals("Dimmer") || cur_switch_type.equals("Fan")) {

				if (cur_switch_type.equals("Dimmer")) {
					if (!type.equals("All") && !type.equals("Normal")) {
						custombuttonview.get(i).setVisibility(View.GONE);
					}
				} else if (cur_switch_type.equals("Fan")) {
					if (!type.equals("All") && !type.equals("Fan")) {
						custombuttonview.get(i).setVisibility(View.GONE);
					}
				}
				findViewById(R.id.switchImageViewBackground).setId(switchImageViewBackground_ID);
				findViewById(R.id.switchImageViewToggle).setId(switchImageViewToggle_ID);
				DynamicSwitch = (RelativeLayout) findViewById(switchLayout_ID);
				DynamicSwitch.setOnClickListener(getOnClickDoSomething(i));
				findViewById(R.id.dimmerIntensity).setId(dimmerIntensity_ID);
				findViewById(R.id.dimmerSeekBar).setId(dimmerSeekBar_ID);
				Drawable thumb = getResources().getDrawable(R.drawable.thumb);
				DimmerSeekBar = (SeekBar) findViewById(dimmerSeekBar_ID);
				DimmerSeekBar.setOnSeekBarChangeListener(this);
				DimmerSeekBar.setThumb(thumb);

				String curDimmerStatus = null;
				String curSwitchStatus = null;
				try {
					curDimmerStatus = arrStatusJSON.getJSONObject(i).getString("DimmerStatus");
					curSwitchStatus = arrStatusJSON.getJSONObject(i).getString("SwitchStatus");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				DimmerSeekBar.setProgress(Integer.parseInt(curDimmerStatus));
				if (curSwitchStatus.equals("ON")) {
					switch_on(switchImageViewBackground_ID);
				} else if (curSwitchStatus.equals("OF")) {
					switch_off(switchImageViewBackground_ID);
				}
			}

			if (cur_switch_type.equals("Curtain")) {
				if (!type.equals("All") && !type.equals("Curtain")) {
					custombuttonview.get(i).setVisibility(View.GONE);
				}
				findViewById(R.id.curtainStopLayout).setId(curtainStopLayout_ID);
				findViewById(R.id.curtainOnLayout).setId(curtainOnLayout_ID);
				findViewById(R.id.curtainOffLayout).setId(curtainOffLayout_ID);

				String curSwitchStatus = null;
				try {
					curSwitchStatus = arrStatusJSON.getJSONObject(i).getString("SwitchStatus");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (curSwitchStatus.equals("ON")) {
					curtain_on(i);
				} else if (curSwitchStatus.equals("OF")) {
					curtain_off(i);
				} else if (curSwitchStatus.equals("ST")) {
					curtain_stop(i);
				}

				DynamicStopSwitch = (LinearLayout) findViewById(curtainStopLayout_ID);
				DynamicStopSwitch.setOnClickListener(getOnClickDoSomethingCurtain(i, "ST"));
				DynamicOnSwitch = (LinearLayout) findViewById(curtainOnLayout_ID);
				DynamicOnSwitch.setOnClickListener(getOnClickDoSomethingCurtain(i, "ON"));
				DynamicOffSwitch = (LinearLayout) findViewById(curtainOffLayout_ID);
				DynamicOffSwitch.setOnClickListener(getOnClickDoSomethingCurtain(i, "OF"));
			}

		}
	}

	public void resetView() {
		try {
			FileInputStream fis = openFileInput(switchStatusJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawSwitchStatusJSON;
			rawSwitchStatusJSON = bufferedReader.readLine();

			JSONObject objStatusJSON = new JSONObject(rawSwitchStatusJSON);
			arrStatusJSON = objStatusJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(room_id)).getJSONArray("Switch");

		} catch (Exception e) {
			e.printStackTrace();
		}

		TextView roomNameTextView = (TextView) findViewById(R.id.roomName);
		roomNameTextView.setText(room_name);

		int switchImageViewBackground_ID = 0;
		int dimmerSeekBar_ID = 0;

		for (int i = 0; i < switch_id.size(); i++) {
			@SuppressWarnings("unused")
			String cur_switch_id = switch_id.get(i);
			String cur_switch_type = switch_type.get(i);
			switchImageViewBackground_ID = 4000 + i;
			dimmerSeekBar_ID = 5000 + i;

			if (cur_switch_type.equals("Normal")) {

				String curSwitchStatus = null;
				try {
					curSwitchStatus = arrStatusJSON.getJSONObject(i).getString("SwitchStatus");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (curSwitchStatus.equals("ON")) {
					switch_on(switchImageViewBackground_ID);
				} else if (curSwitchStatus.equals("OF")) {
					switch_off(switchImageViewBackground_ID);
				}

			}
			if (cur_switch_type.equals("Dimmer") || cur_switch_type.equals("Fan")) {
				String curDimmerStatus = null;
				String curSwitchStatus = null;
				try {
					curDimmerStatus = arrStatusJSON.getJSONObject(i).getString("DimmerStatus");
					curSwitchStatus = arrStatusJSON.getJSONObject(i).getString("SwitchStatus");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				DimmerSeekBar = (SeekBar) findViewById(dimmerSeekBar_ID);

				DimmerSeekBar.setProgress(Integer.parseInt(curDimmerStatus));
				if (curSwitchStatus.equals("ON")) {
					switch_on(switchImageViewBackground_ID);
				} else if (curSwitchStatus.equals("OF")) {
					switch_off(switchImageViewBackground_ID);
				}

			}

			if (cur_switch_type.equals("Curtain")) {
				String curSwitchStatus = null;
				try {
					curSwitchStatus = arrStatusJSON.getJSONObject(i).getString("SwitchStatus");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (curSwitchStatus.equals("ON")) {
					curtain_on(i);
				} else if (curSwitchStatus.equals("OF")) {
					curtain_off(i);
				} else if (curSwitchStatus.equals("ST")) {
					curtain_stop(i);
				}

			}

		}

	}

	View.OnClickListener getOnClickDoSomething(final int id) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int switch_id = id;
				String Room = "R" + String.format("%02d", Integer.parseInt(room_id));
				String Switch = "S" + String.format("%02d", switch_id);
				String curSwitchStatus = null;
				try {
					FileInputStream fis = openFileInput(switchStatusJSON);
					InputStreamReader isr = new InputStreamReader(fis);
					BufferedReader bufferedReader = new BufferedReader(isr);
					String rawSwitchStatusJSON;
					rawSwitchStatusJSON = bufferedReader.readLine();

					JSONObject objStatusJSON = new JSONObject(rawSwitchStatusJSON);
					arrStatusJSON = objStatusJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(room_id)).getJSONArray("Switch");
					curSwitchStatus = arrStatusJSON.getJSONObject(id).getString("SwitchStatus");
				} catch (Exception e) {
					e.printStackTrace();
				}
				String Value = null;
				if (curSwitchStatus.equals("ON")) {
					Value = "OF";
				} else if (curSwitchStatus.equals("OF")) {
					Value = "ON";
				}
				String rawRequestCommand = Room + Switch + Value;
				String JSON = null;
				try {
					JSONObject objJSON = new JSONObject();
					objJSON.put("Request", rawRequestCommand);
					objJSON.put("RequestType", "Normal");
					JSON = objJSON.toString();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				messagePublish("request/switchControl", JSON);
			}
		};

	}

	View.OnClickListener getOnClickDoSomethingCurtain(final int id, final String Value) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int switch_id = id;
				String Room = "R" + String.format("%02d", Integer.parseInt(room_id));
				String Switch = "S" + String.format("%02d", switch_id);
				// String Value = "ST";
				String rawRequestCommand = Room + Switch + Value;

				String JSON = null;
				try {
					JSONObject objJSON = new JSONObject();
					objJSON.put("Request", rawRequestCommand);
					objJSON.put("RequestType", "Normal");
					JSON = objJSON.toString();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				messagePublish("request/switchControl", JSON);
			}
		};
	}

	public void messagePublish(String TOPIC, String MESSAGE) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("com.letsandy.publishMQTT");
		broadcastIntent.putExtra("TOPIC", TOPIC);
		broadcastIntent.putExtra("MESSAGE", MESSAGE);
		sendBroadcast(broadcastIntent);
	}

	@Override
	public void onProgressChanged(SeekBar dimmerSeekBar, int progress, boolean fromUser) {

		int id = dimmerSeekBar.getId() % 5000;
		TextView dimmerIntensity = (TextView) findViewById(6000 + id);
		dimmerIntensity.setText(Integer.toString(progress));
		curProgress = progress;
	}

	@Override
	public void onStartTrackingTouch(SeekBar dimmerSeekBar) {
	}

	@Override
	@SuppressLint("DefaultLocale")
	public void onStopTrackingTouch(SeekBar dimmerSeekBar) {
		int switch_id = dimmerSeekBar.getId() % 5000;
		String Room = "R" + String.format("%02d", Integer.parseInt(room_id));
		String Switch = "S" + String.format("%02d", switch_id);
		String Value = String.format("%02d", curProgress);
		String rawRequestCommand = Room + Switch + Value;
		String dimmerJSON = null;
		try {
			JSONObject objJSON = new JSONObject();
			objJSON.put("Request", rawRequestCommand);
			objJSON.put("RequestType", "Normal");
			dimmerJSON = objJSON.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		messagePublish("request/switchControl", dimmerJSON);

	}

	void switch_on(int switchImageViewBackground_ID) {
		int BackgroundColor = getResources().getColor(R.color.holo_blue);
		findViewById(switchImageViewBackground_ID).setBackgroundColor(BackgroundColor);
		View v1 = findViewById((switchImageViewBackground_ID + 4000));
		v1.setVisibility(View.VISIBLE);

	}

	void switch_off(int switchImageViewBackground_ID) {
		int BackgroundColor = getResources().getColor(R.color.foreground_light);
		findViewById(switchImageViewBackground_ID).setBackgroundColor(BackgroundColor);
		View v1 = findViewById((switchImageViewBackground_ID + 4000));
		v1.setVisibility(View.GONE);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	void curtain_off(int id) {
		int curtainStopLayout_ID = 9000 + id;
		int curtainOnLayout_ID = 10000 + id;
		int curtainOffLayout_ID = 11000 + id;
		Drawable BackgroundColor1 = getResources().getDrawable(R.drawable.border_fill);
		Drawable BackgroundColor2 = getResources().getDrawable(R.drawable.border);
		final int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			findViewById(curtainOnLayout_ID).setBackgroundDrawable(BackgroundColor2);
			findViewById(curtainOffLayout_ID).setBackgroundDrawable(BackgroundColor1);
			findViewById(curtainStopLayout_ID).setBackgroundDrawable(BackgroundColor2);
		} else {
			findViewById(curtainOnLayout_ID).setBackground(BackgroundColor2);
			findViewById(curtainOffLayout_ID).setBackground(BackgroundColor1);
			findViewById(curtainStopLayout_ID).setBackground(BackgroundColor2);
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	void curtain_on(int id) {
		int curtainStopLayout_ID = 9000 + id;
		int curtainOnLayout_ID = 10000 + id;
		int curtainOffLayout_ID = 11000 + id;
		Drawable BackgroundColor1 = getResources().getDrawable(R.drawable.border_fill);
		Drawable BackgroundColor2 = getResources().getDrawable(R.drawable.border);
		final int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			findViewById(curtainOnLayout_ID).setBackgroundDrawable(BackgroundColor1);
			findViewById(curtainOffLayout_ID).setBackgroundDrawable(BackgroundColor2);
			findViewById(curtainStopLayout_ID).setBackgroundDrawable(BackgroundColor2);
		} else {
			findViewById(curtainOnLayout_ID).setBackground(BackgroundColor1);
			findViewById(curtainOffLayout_ID).setBackground(BackgroundColor2);
			findViewById(curtainStopLayout_ID).setBackground(BackgroundColor2);
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	void curtain_stop(int id) {
		int curtainStopLayout_ID = 9000 + id;
		int curtainOnLayout_ID = 10000 + id;
		int curtainOffLayout_ID = 11000 + id;
		Drawable BackgroundColor1 = getResources().getDrawable(R.drawable.border_fill);
		Drawable BackgroundColor2 = getResources().getDrawable(R.drawable.border);
		final int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			findViewById(curtainStopLayout_ID).setBackgroundDrawable(BackgroundColor1);
			findViewById(curtainOnLayout_ID).setBackgroundDrawable(BackgroundColor2);
			findViewById(curtainOffLayout_ID).setBackgroundDrawable(BackgroundColor2);

		} else {
			findViewById(curtainStopLayout_ID).setBackground(BackgroundColor1);
			findViewById(curtainOnLayout_ID).setBackground(BackgroundColor2);
			findViewById(curtainOffLayout_ID).setBackground(BackgroundColor2);

		}
	}

	public class updateSwitchView extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			resetView();
		}

	}

	public class fullUpdateSwitchView extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateView();
		}

	}

}
