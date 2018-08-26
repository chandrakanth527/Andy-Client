package com.letsandy.vavis;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

@SuppressLint({ "InflateParams", "DefaultLocale" })
public class ServiceGridAdapter extends BaseAdapter {
	private FragmentServices fragment;
	private final String[] gridValues;
	private final String[] serviceIcons;
	ViewHolder viewHolder;

	public ServiceGridAdapter(FragmentServices fragmentServices, String[] gridValues, String[] gridIcons) {
		this.fragment = fragmentServices;
		this.gridValues = gridValues;
		this.serviceIcons = gridIcons;
	}

	@Override
	public int getCount() {
		return gridValues.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	// Number of times getView method call depends upon gridValues.length

	@SuppressLint("ViewTag")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = new View(fragment.getActivity());
			convertView = inflater.inflate(R.layout.servicegridsingle, null);
			viewHolder = new ViewHolder();
			viewHolder.Image = (ImageView) convertView.findViewById(R.id.grid_image);
			viewHolder.Text = (TextView) convertView.findViewById(R.id.grid_text);
			viewHolder.LinearLayout = (LinearLayout) convertView.findViewById(R.id.gridsingle);

		} else {
			viewHolder = (ViewHolder) convertView.getTag(R.id.serviceGridView);
		}
		convertView.setTag(R.id.serviceGridView, viewHolder);
		viewHolder.Text.setText(gridValues[position]);

		String serviceIcon = serviceIcons[position];
		int resID = fragment.getActivity().getResources().getIdentifier(serviceIcon, "drawable", fragment.getActivity().getPackageName());
		viewHolder.Image.setImageResource(resID);

		viewHolder.LinearLayout.setTag(position);
		
		viewHolder.LinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				String type = gridValues[position];
				if (type.equals("Switch")) {
					Intent roomintent = new Intent(fragment.getActivity().getApplicationContext(), RoomActivity.class);
					fragment.getActivity().startActivity(roomintent);
				} else if (type.equals("Schedule")) {
					Intent scheduleintent = new Intent(fragment.getActivity().getApplicationContext(), ScheduleActivity.class);
					fragment.getActivity().startActivity(scheduleintent);

				} else if (type.equals("Scene")) {
					Intent sceneIntent = new Intent(fragment.getActivity().getApplicationContext(), EditSceneActivity.class);
					fragment.getActivity().startActivity(sceneIntent);
				} else if (type.equals("CCTV")) {

				} else if (type.equals("Voice")) {
					if (isConnected()) {
						Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
						intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
						intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your command");
						try {
							fragment.getActivity().startActivityForResult(intent, 99);
						} catch (Exception e) {
							Toast.makeText(fragment.getActivity().getApplicationContext(), "Oops something went wrong!", Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(fragment.getActivity().getApplicationContext(), "Plese Connect to Internet", Toast.LENGTH_LONG).show();
					}

				} else if (type.equals("Remotes")) {
					Intent sceneIntent = new Intent(fragment.getActivity().getApplicationContext(), RemoteActivity.class);
					fragment.getActivity().startActivity(sceneIntent);

				} else if (type.equals("Security")) {
					SharedPreferences prefs = fragment.getActivity().getApplicationContext().getSharedPreferences("deviceId", 0);
					String deviceId = prefs.getString("deviceId", "");
					String Connection_Type = "LOCAL";
					String clientHandle = deviceId + ":" + Connection_Type;
					Connection c = Connections.getInstance(fragment.getActivity().getApplicationContext()).getConnection(clientHandle);
					String msg3 = null;
					if (c.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED)) {
						msg3 = "MQTT Local connected";
					} else if (c.getConnectionStatus().equals(Connection.ConnectionStatus.DISCONNECTED)) {
						msg3 = "MQTT Local disconnected";
					} else if (c.getConnectionStatus().equals(Connection.ConnectionStatus.NONE)) {

						msg3 = "MQTT Local None";
					} else if (c.getConnectionStatus().equals(Connection.ConnectionStatus.ERROR)) {

						msg3 = "MQTT Local ERROR";
					} else {
						msg3 = "MQTT Local Unknown";
					}

					Connection_Type = "CLOUD";
					String clientHandle1 = deviceId + ":" + Connection_Type;
					Connection c1 = Connections.getInstance(fragment.getActivity().getApplicationContext()).getConnection(clientHandle1);
					String msg4 = null;
					if (c1.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED)) {
						msg4 = "MQTT Cloud connected";
					} else if (c1.getConnectionStatus().equals(Connection.ConnectionStatus.DISCONNECTED)) {
						msg4 = "MQTT Cloud disconnected";
					} else if (c1.getConnectionStatus().equals(Connection.ConnectionStatus.NONE)) {

						msg4 = "MQTT Cloud None";
					} else if (c1.getConnectionStatus().equals(Connection.ConnectionStatus.ERROR)) {

						msg4 = "MQTT Cloud ERROR";
					} else {
						msg4 = "MQTT Cloud Unknown";
					}
					String msg1;
					String msg2;
					if (isMyServiceRunning(MessagingService.class, fragment.getActivity().getBaseContext())) {
						msg1 = "Messaging service is running";
					} else {
						msg1 = "Messaging service is not running";
					}

					if (isMyServiceRunning(org.eclipse.paho.android.service.MqttService.class, fragment.getActivity().getBaseContext())) {
						msg2 = "MQTT service is running";
					} else {
						msg2 = "MQTT service is not running";
						// context.startService(new Intent(context,
						// MessagingService.class));
					}
					Toast.makeText(fragment.getActivity().getBaseContext(), msg1 + "\n" + msg2 + "\n" + msg3 + "\n" + msg4 + "\n" + deviceId, Toast.LENGTH_SHORT).show();

				} else if (type.equals("Settings")) {
					Intent settingsintent = new Intent(fragment.getActivity().getApplicationContext(), SettingsActivity.class);
					fragment.getActivity().startActivity(settingsintent);

				}

				/*
				 * switch (position) { case 0: Intent roomintent = new
				 * Intent(fragment.getActivity().getApplicationContext(),
				 * RoomActivity.class);
				 * fragment.getActivity().startActivity(roomintent); break; case
				 * 1: Intent scheduleintent = new
				 * Intent(fragment.getActivity().getApplicationContext(),
				 * ScheduleActivity.class);
				 * fragment.getActivity().startActivity(scheduleintent); break;
				 * case 2: Intent sceneIntent = new
				 * Intent(fragment.getActivity().getApplicationContext(),
				 * EditSceneActivity.class);
				 * fragment.getActivity().startActivity(sceneIntent); break;
				 * case 3: // messagePublish("hello","blah blah"); break; case
				 * 4: if (isConnected()) { Intent intent = new
				 * Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				 * intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				 * RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				 * intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				 * "Speak your command"); try {
				 * fragment.getActivity().startActivityForResult(intent, 99); }
				 * catch (Exception e) {
				 * Toast.makeText(fragment.getActivity().getApplicationContext
				 * (), "Oops something went wrong!", Toast.LENGTH_LONG).show();
				 * } } else {
				 * Toast.makeText(fragment.getActivity().getApplicationContext
				 * (), "Plese Connect to Internet", Toast.LENGTH_LONG).show(); }
				 * break; case 5: // Intent broadcastIntent1 = new Intent(); //
				 * broadcastIntent1.setAction("com.letsandy.reconnectMQTT"); //
				 * fragment.getActivity().sendBroadcast(broadcastIntent1);
				 * 
				 * // Intent remoteintent = new //
				 * Intent(fragment.getActivity().getApplicationContext(), //
				 * RemoteActivity.class); //
				 * fragment.getActivity().startActivity(remoteintent); break;
				 * case 6: // Intent broadcastIntent = new Intent(); //
				 * broadcastIntent.setAction("com.letsandy.disconnectMQTT"); //
				 * fragment.getActivity().sendBroadcast(broadcastIntent);
				 * 
				 * SharedPreferences prefs =
				 * fragment.getActivity().getApplicationContext
				 * ().getSharedPreferences("deviceId", 0); String deviceId =
				 * prefs.getString("deviceId", ""); String Connection_Type =
				 * "LOCAL"; String clientHandle = deviceId + ":" +
				 * Connection_Type; Connection c =
				 * Connections.getInstance(fragment
				 * .getActivity().getApplicationContext
				 * ()).getConnection(clientHandle); String msg3 = null; if
				 * (c.getConnectionStatus
				 * ().equals(Connection.ConnectionStatus.CONNECTED)) { msg3 =
				 * "MQTT Local connected"; } else if
				 * (c.getConnectionStatus().equals
				 * (Connection.ConnectionStatus.DISCONNECTED)) { msg3 =
				 * "MQTT Local disconnected"; } else if
				 * (c.getConnectionStatus().
				 * equals(Connection.ConnectionStatus.NONE)) {
				 * 
				 * msg3 = "MQTT Local None"; } else if
				 * (c.getConnectionStatus().equals
				 * (Connection.ConnectionStatus.ERROR)) {
				 * 
				 * msg3 = "MQTT Local ERROR"; } else { msg3 =
				 * "MQTT Local Unknown"; }
				 * 
				 * Connection_Type = "CLOUD"; String clientHandle1 = deviceId +
				 * ":" + Connection_Type; Connection c1 =
				 * Connections.getInstance
				 * (fragment.getActivity().getApplicationContext
				 * ()).getConnection(clientHandle1); String msg4 = null; if
				 * (c1.getConnectionStatus
				 * ().equals(Connection.ConnectionStatus.CONNECTED)) { msg4 =
				 * "MQTT Cloud connected"; } else if
				 * (c1.getConnectionStatus().equals
				 * (Connection.ConnectionStatus.DISCONNECTED)) { msg4 =
				 * "MQTT Cloud disconnected"; } else if
				 * (c1.getConnectionStatus()
				 * .equals(Connection.ConnectionStatus.NONE)) {
				 * 
				 * msg4 = "MQTT Cloud None"; } else if
				 * (c1.getConnectionStatus().
				 * equals(Connection.ConnectionStatus.ERROR)) {
				 * 
				 * msg4 = "MQTT Cloud ERROR"; } else { msg4 =
				 * "MQTT Cloud Unknown"; } String msg1; String msg2; if
				 * (isMyServiceRunning(MessagingService.class,
				 * fragment.getActivity().getBaseContext())) { msg1 =
				 * "Messaging service is running"; } else { msg1 =
				 * "Messaging service is not running"; }
				 * 
				 * if
				 * (isMyServiceRunning(org.eclipse.paho.android.service.MqttService
				 * .class, fragment.getActivity().getBaseContext())) { msg2 =
				 * "MQTT service is running"; } else { msg2 =
				 * "MQTT service is not running"; // context.startService(new
				 * Intent(context, // MessagingService.class)); }
				 * Toast.makeText(fragment.getActivity().getBaseContext(), msg1
				 * + "\n" + msg2 + "\n" + msg3 + "\n" + msg4 + "\n" + deviceId,
				 * Toast.LENGTH_SHORT).show();
				 * 
				 * break; case 7: Intent settingsintent = new
				 * Intent(fragment.getActivity().getApplicationContext(),
				 * SettingsActivity.class);
				 * fragment.getActivity().startActivity(settingsintent);
				 * 
				 * break; }
				 */

			}
		});

		return convertView;
	}

	public boolean isConnected() {
		ConnectivityManager cm = (ConnectivityManager) fragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = cm.getActiveNetworkInfo();
		if (net != null && net.isAvailable() && net.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public void messagePublish(String TOPIC, String MESSAGE) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("com.letsandy.publishMQTT");
		broadcastIntent.putExtra("TOPIC", TOPIC);
		broadcastIntent.putExtra("MESSAGE", MESSAGE);
		fragment.getActivity().sendBroadcast(broadcastIntent);
	}

	private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
