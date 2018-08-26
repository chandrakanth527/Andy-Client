package com.letsandy.vavis;

import com.nullwire.trace.ExceptionHandler;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

public class BaseActivity extends Activity {
	static boolean background = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExceptionHandler.register(this, "http://mqtt.letsandy.com/crash/server.php");

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
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("com.letsandy.reconnectMQTT");
		sendBroadcast(broadcastIntent);
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
		if (c != null && c1 != null) {
			if (!c.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED) && !c1.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED)) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	
}
