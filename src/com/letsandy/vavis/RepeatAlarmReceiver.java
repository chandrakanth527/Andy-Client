package com.letsandy.vavis;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class RepeatAlarmReceiver extends BroadcastReceiver {
	public static final String MY_PREFS_NAME = "ServiceCount";

	public void onReceive(Context context, Intent intent) {
		String msg1 = "";
		String msg2 = "";

		if (isMyServiceRunning(MessagingService.class, context)) {
			msg1 = "Messaging service is running";
		} else {
			msg1 = "Messaging service is not running";
			context.startService(new Intent(context, MessagingService.class));
		}
		if (isMyServiceRunning(org.eclipse.paho.android.service.MqttService.class, context)) {
			msg2 = "MQTT service is running";
		} else {
			msg2 = "MQTT service is not running";
			// context.startService(new Intent(context,
			// MessagingService.class));
		}

		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wInfo = wifiManager.getConnectionInfo();
		String deviceId = wInfo.getMacAddress();
		if (deviceId == null) {
			deviceId = MqttAsyncClient.generateClientId();
		}
		String Connection_Type = "CLOUD";
		String clientHandle = deviceId + ":" + Connection_Type;
		Connection c = Connections.getInstance(context.getApplicationContext()).getConnection(clientHandle);
		if (!c.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED)) {
		//	Intent broadcastIntent = new Intent();
		//	broadcastIntent.setAction("com.letsandy.reconnectMQTT");
		//	context.sendBroadcast(broadcastIntent);
		}
		Toast.makeText(context, msg1 + "\n" + msg2, Toast.LENGTH_SHORT).show();
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