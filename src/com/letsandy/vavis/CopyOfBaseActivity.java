package com.letsandy.vavis;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

public class CopyOfBaseActivity extends Activity {
	static boolean background = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		clientConnectedMQTT clientConnectedMQTT = new clientConnectedMQTT();
		IntentFilter intentClientConnectedMQTT = new IntentFilter("com.letsandy.clientConnectedMQTT");
		registerReceiver(clientConnectedMQTT, intentClientConnectedMQTT);

		connectionLostMQTT connectionLostMQTT = new connectionLostMQTT();
		IntentFilter intentConnectionLostMQTT = new IntentFilter("com.letsandy.connectionLostMQTT");
		registerReceiver(connectionLostMQTT, intentConnectionLostMQTT);
	}

	protected void onResume() {
		super.onResume();
		changeMenuIcon();
		Log.e("Base Activity", "inside reconnect " + getApplicationContext());
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("deviceId", 0);
		String deviceId = prefs.getString("deviceId", "");
		String Connection_Type = "LOCAL";
		String clientHandle = deviceId + ":" + Connection_Type;

		Connection c = Connections.getInstance(getApplicationContext()).getConnection(clientHandle);
		Log.e("base activity", c.getConnectionStatus().toString() + clientHandle);
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
		Connection c1 = Connections.getInstance(getApplicationContext()).getConnection(clientHandle1);
		String msg4 = null;
		Log.e("base activity", c1.getConnectionStatus().toString());
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

		// Toast.makeText(getBaseContext(), c.getConnectionStatus().toString() +
		// " " + c1.getConnectionStatus().toString(),
		// Toast.LENGTH_SHORT).show();
		String msg1;
		String msg2;
		if (isMyServiceRunning(MessagingService.class, getBaseContext())) {
			msg1 = "Messaging service is running ";
		} else {
			msg1 = "Messaging service is not running";
		}

		if (isMyServiceRunning(org.eclipse.paho.android.service.MqttService.class, getBaseContext())) {
			msg2 = "MQTT service is running";
		} else {
			msg2 = "MQTT service is not running";
		}
		Toast.makeText(getBaseContext(), msg1 + "\n" + msg2 + "\n" + msg3 + "\n" + msg4 + "\n" + deviceId, Toast.LENGTH_LONG).show();

		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("com.letsandy.reconnectMQTT");
		sendBroadcast(broadcastIntent);
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

	protected void onUserLeaveHint() {
	}

	public class connectionLostMQTT extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			changeMenuIcon();
		}
	}

	public class clientConnectedMQTT extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			changeMenuIcon();
		}
	}

	public void changeMenuIcon() {
		if (mqttConnectionStatus(getApplicationContext())) {
			findViewById(R.id.connectStatus).setBackgroundResource(R.drawable.ic_cloud_connected);
		} else {
			findViewById(R.id.connectStatus).setBackgroundResource(R.drawable.ic_cloud_disconnected);
		}
	}

	public boolean mqttConnectionStatus(Context context) {
		SharedPreferences prefs = context.getSharedPreferences("deviceId", 0);
		String deviceId = prefs.getString("deviceId", "");
		String Connection_Type = "LOCAL";
		String clientHandle = deviceId + ":" + Connection_Type;
		String Connection_Type1 = "CLOUD";
		String clientHandle1 = deviceId + ":" + Connection_Type1;
		Connection c = Connections.getInstance(context).getConnection(clientHandle);
		Connection c1 = Connections.getInstance(context).getConnection(clientHandle1);
		if (!c.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED) && !c1.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED)) {
			return false;
		} else {
			return true;
		}
	}
}
