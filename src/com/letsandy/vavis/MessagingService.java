package com.letsandy.vavis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.letsandy.vavis.ActionListener.Action;
import com.nullwire.trace.ExceptionHandler;

@SuppressLint({ "DefaultLocale", "NewApi" })
public class MessagingService extends Service {
	private MqttAndroidClient mqttClientCloud;
	private MqttAndroidClient mqttClientLocal;
	ActionListener callbackCloud = null;
	ActionListener callbackLocal = null;
	static boolean restartCLOUD = false;
	static boolean restartLOCAL = false;
	static String deviceId;
	publishMQTT publishMQTT;
	disconnectMQTT disconnectMQTT;
	connectMQTT connectMQTT;
	reconnectMQTT reconnectMQTT;
	clientConnectedMQTT clientConnectedMQTT;
	disconnectSuccesfulMQTT disconnectSuccesfulMQTT;
	connectionLostMQTT connectionLostMQTT;
	messageArrivedMQTT messageArrivedMQTT;
	public static final String MY_PREFS_NAME = "ServiceCount";
	String Reboot = "FALSE";
	String Connection_Type = null;
	String ID = null;
	public static String CONNECTION_STATUS = null;

	public void onResume() {
	}

	public void onCreate() {
		
		ExceptionHandler.register(this, "http://mqtt.letsandy.com/crash/server.php");
		setClientID();
		publishMQTT publishMQTT = new publishMQTT();
		IntentFilter intentPublishMQTT = new IntentFilter("com.letsandy.publishMQTT");
		registerReceiver(publishMQTT, intentPublishMQTT);

		disconnectMQTT disconnectMQTT = new disconnectMQTT();
		IntentFilter intentDisconnectMQTT = new IntentFilter("com.letsandy.disconnectMQTT");
		registerReceiver(disconnectMQTT, intentDisconnectMQTT);

		connectMQTT connectMQTT = new connectMQTT();
		IntentFilter intentConnectMQTT = new IntentFilter("com.letsandy.connectMQTT");
		registerReceiver(connectMQTT, intentConnectMQTT);

		reconnectMQTT reconnectMQTT = new reconnectMQTT();
		IntentFilter intentReConnectMQTT = new IntentFilter("com.letsandy.reconnectMQTT");
		registerReceiver(reconnectMQTT, intentReConnectMQTT);

		connectionLostMQTT connectionLostMQTT = new connectionLostMQTT();
		IntentFilter intentConnectionLostMQTT = new IntentFilter("com.letsandy.connectionLostMQTT");
		registerReceiver(connectionLostMQTT, intentConnectionLostMQTT);

		clientConnectedMQTT clientConnectedMQTT = new clientConnectedMQTT();
		IntentFilter intentClientConnectedMQTT = new IntentFilter("com.letsandy.clientConnectedMQTT");
		registerReceiver(clientConnectedMQTT, intentClientConnectedMQTT);

		disconnectSuccesfulMQTT disconnectSuccesfulMQTT = new disconnectSuccesfulMQTT();
		IntentFilter intentDisconnectSuccesfulMQTT = new IntentFilter("com.letsandy.disconnectSuccesfulMQTT");
		registerReceiver(disconnectSuccesfulMQTT, intentDisconnectSuccesfulMQTT);

		messageArrivedMQTT messageArrivedMQTT = new messageArrivedMQTT();
		IntentFilter intentMessageArrivedMQTT = new IntentFilter("com.letsandy.messageArrivedMQTT");
		registerReceiver(messageArrivedMQTT, intentMessageArrivedMQTT);

		NetworkChangeReceiver NetworkChangeReceiver = new NetworkChangeReceiver();
		IntentFilter intentNetworkChangeReceiver = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(NetworkChangeReceiver, intentNetworkChangeReceiver);

		String configJSON = "configJSON.cfg";
		try {
			FileInputStream fis = openFileInput(configJSON);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader bufferedReader = new BufferedReader(isr);
			String rawConfigJSON;
			rawConfigJSON = bufferedReader.readLine();
			JSONObject objJSON = new JSONObject(rawConfigJSON);
			ID = objJSON.getString("ID");

		} catch (Exception e) {
			e.printStackTrace();
		}

		
		connectMQTT("CLOUD");
		connectMQTT("LOCAL");

	}

	public class NetworkChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			Log.e("Network Available ", "Connectivity changed");
			final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			if (wifi.isConnected() || mobile.isConnected()) {
				Log.e("Some Network", "Connectivity changed");
				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction("com.letsandy.reconnectMQTT");
				sendBroadcast(broadcastIntent);
			}

		}
	}

	private void setClientID() {
		deviceId = MqttAsyncClient.generateClientId();

		SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("deviceId", Context.MODE_PRIVATE).edit();
		editor.putString("deviceId", deviceId);
		editor.commit();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	public void onDestroy() {
		super.onDestroy();
		if (publishMQTT != null) {
			unregisterReceiver(publishMQTT);
		}
		if (disconnectMQTT != null) {
			unregisterReceiver(disconnectMQTT);
		}
		if (clientConnectedMQTT != null) {
			unregisterReceiver(clientConnectedMQTT);
		}
		if (messageArrivedMQTT != null) {
			unregisterReceiver(messageArrivedMQTT);
		}
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		Intent restartService = new Intent(getApplicationContext(), this.getClass());
		restartService.setPackage(getPackageName());
		PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);

	}

	public void connectMQTT(String Connection_Type) {
		Log.e("connect", Connection_Type);
		String SERVER = null;
		String BROKER_ADDRESS = null;
		String clientHandle = deviceId + ":" + Connection_Type;

		MqttConnectOptions conOpt = new MqttConnectOptions();
		conOpt.setCleanSession(true);
		conOpt.setConnectionTimeout(ConfigClass.CONN_TIMEOUT);
		conOpt.setKeepAliveInterval(ConfigClass.CONN_KEEPALIVE);
		conOpt.setUserName("andy");
		conOpt.setPassword("andy!@#$%".toCharArray());
		conOpt.setAutomaticReconnect(false);

		if (Connection_Type.equals("CLOUD")) {
			SERVER = ConfigClass.CLOUD_BROKER_URI;
			BROKER_ADDRESS = ConfigClass.CLOUD_BROKER_ADDRESS;
			if (mqttClientCloud == null) {
				Log.e("Client optinos present", "client present");
				MemoryPersistence mem = new MemoryPersistence();
				mqttClientCloud = new MqttAndroidClient(this, SERVER, clientHandle, mem);
				Connection con = new Connection(clientHandle, clientHandle, BROKER_ADDRESS, ConfigClass.BROKER_PORT, this, mqttClientCloud, false);
				String[] actionArgs = new String[1];
				actionArgs[0] = clientHandle;
				callbackCloud = new ActionListener(this, ActionListener.Action.CONNECT, clientHandle, actionArgs);
				mqttClientCloud.setCallback(new MqttCallbackHandler(this, clientHandle));
				mqttClientCloud.setTraceCallback(new MqttTraceCallback());
				con.addConnectionOptions(conOpt);
				Connections.getInstance(getApplicationContext()).addConnection(con);
			}
			try {
				mqttClientCloud.connect(conOpt, null, callbackCloud);
			} catch (org.eclipse.paho.client.mqttv3.MqttException e) {
				e.printStackTrace();
			}

		} else {
			SharedPreferences mPrefs1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String gatewayIP = mPrefs1.getString("gatewayIP", "192.168.1.3");
			BROKER_ADDRESS = gatewayIP;
			SERVER = "tcp://" + BROKER_ADDRESS + ":1883";

			if (mqttClientLocal == null) {
				Log.e("Client optinos present", "client present");
				MemoryPersistence mem = new MemoryPersistence();
				mqttClientLocal = new MqttAndroidClient(this, SERVER, clientHandle, mem);
				Connection con = new Connection(clientHandle, clientHandle, BROKER_ADDRESS, ConfigClass.BROKER_PORT, this, mqttClientLocal, false);
				String[] actionArgs = new String[1];
				actionArgs[0] = clientHandle;
				callbackLocal = new ActionListener(this, ActionListener.Action.CONNECT, clientHandle, actionArgs);
				mqttClientLocal.setCallback(new MqttCallbackHandler(this, clientHandle));
				mqttClientLocal.setTraceCallback(new MqttTraceCallback());
				con.addConnectionOptions(conOpt);
				Connections.getInstance(getApplicationContext()).addConnection(con);
			}
			try {
				mqttClientLocal.connect(conOpt, null, callbackLocal);
			} catch (org.eclipse.paho.client.mqttv3.MqttException e) {
				e.printStackTrace();
			}

		}

	}

	public void publishMQTT(String TOPIC, String MESSAGE, String connection_type) {
		Log.e("publish", TOPIC);
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("deviceId", 0);
		String deviceId = prefs.getString("deviceId", "");
		String clientHandle = deviceId + ":" + connection_type;

		Connection c = Connections.getInstance(getApplicationContext()).getConnection(clientHandle);
		if (c.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED)) {
			try {
				c.getClient().publish(ID + "/" + TOPIC, MESSAGE.getBytes(), 0, false, null, new ActionListener(this, Action.PUBLISH, clientHandle, "12345"));
			} catch (MqttSecurityException e) {
			} catch (MqttException e) {
			}

		} else if (c.getConnectionStatus().equals(Connection.ConnectionStatus.NONE)) {
			Toast.makeText(this.getBaseContext(), "MQTT.NONE", Toast.LENGTH_SHORT).show();
		} else if (c.getConnectionStatus().equals(Connection.ConnectionStatus.ERROR)) {
			Toast.makeText(this.getBaseContext(), "MQTT.ERROR", Toast.LENGTH_SHORT).show();
		} else if (c.getConnectionStatus().equals(Connection.ConnectionStatus.DISCONNECTED)) {
			Toast.makeText(this.getBaseContext(), "MQTT.DISCONNECTED", Toast.LENGTH_SHORT).show();
		} else {

		}

	}

	public class publishMQTT extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle intentExtra = intent.getExtras();
			String TOPIC = intentExtra.getString("TOPIC");
			String MESSAGE = intentExtra.getString("MESSAGE");
			SharedPreferences prefs = getApplicationContext().getSharedPreferences("deviceId", 0);
			String deviceId = prefs.getString("deviceId", "");
			String clientHandle = deviceId + ":" + "LOCAL";
			Connection c = Connections.getInstance(getApplicationContext()).getConnection(clientHandle);
			if (c.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED)) {
				publishMQTT(TOPIC, MESSAGE, "LOCAL");
			} else {
				publishMQTT(TOPIC, MESSAGE, "CLOUD");
			}

		}

	}

	public void subscribeMQTT(String TOPIC, String clientHandle) {
		Connection c = Connections.getInstance(getApplicationContext()).getConnection(clientHandle);
		try {
			int qos = 0;
			Log.e("subscribe", TOPIC);
			c.getClient().subscribe(ID + "/" + TOPIC, qos, null, new ActionListener(this, Action.SUBSCRIBE, clientHandle, ""));
		} catch (MqttSecurityException e) {
		} catch (MqttException e) {
		}
	}

	public class clientConnectedMQTT extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String clientHandle = intent.getStringExtra("clientHandle");
			for (String TOPIC : ConfigClass.TOPICS) {
				subscribeMQTT(TOPIC, clientHandle);
			}

			SharedPreferences prefs = getApplicationContext().getSharedPreferences("deviceId", 0);
			String deviceId = prefs.getString("deviceId", "");
			String clientHandleLOCAL = deviceId + ":" + "LOCAL";
			String clientHandleCLOUD = deviceId + ":" + "CLOUD";

			Log.e("clientConnected", clientHandle);
			if (clientHandle.equals(clientHandleLOCAL)) {
				publishMQTT("request/completeStatus", "completeStatus", "LOCAL");
			} else if (clientHandle.equals(clientHandleCLOUD)) {
				publishMQTT("request/completeStatus", "completeStatus", "CLOUD");
			}

		}

	}

	public void messagePublish(String TOPIC, String MESSAGE) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("com.letsandy.publishMQTT");
		broadcastIntent.putExtra("TOPIC", TOPIC);
		broadcastIntent.putExtra("MESSAGE", MESSAGE);
		sendBroadcast(broadcastIntent);
	}

	public class disconnectMQTT extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

		}

	}

	public class connectionLostMQTT extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction("com.letsandy.reconnectMQTT");
			sendBroadcast(broadcastIntent);
		}

	}

	public void disconnectMQTT(String clientHandle) {

		Connection c = Connections.getInstance(getApplicationContext()).getConnection(clientHandle);
		Log.e("disconnect123", clientHandle);
		Log.e("disconnect123", c.getConnectionStatus().toString());
		Log.e("call xyz", "Called here");

		if (c != null) {
			if (c.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED)) {
				Log.e("disconnect", "here");
				try {
					c.getClient().disconnect(null, new ActionListener(this, Action.DISCONNECT, clientHandle, "1234"));
				} catch (MqttException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public class reconnectMQTT1 extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			connectMQTT("LOCAL");
			connectMQTT("CLOUD");
		}
	}

	public class reconnectMQTT extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("reconnect", "inside reconnect " + getApplicationContext());
			String clientHandleLOCAL = deviceId + ":" + "LOCAL";
			String clientHandleCLOUD = deviceId + ":" + "CLOUD";

			Connection c1 = Connections.getInstance(getApplicationContext()).getConnection(clientHandleLOCAL);
			Connection c2 = Connections.getInstance(getApplicationContext()).getConnection(clientHandleCLOUD);

			if (c1 != null) {
				Log.e("reconnect", c1.getConnectionStatus().toString() + " " + clientHandleLOCAL);
				if (!c1.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED) && !c1.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTING)) {
					connectMQTT("LOCAL");
				}
			} else {
				connectMQTT("LOCAL");
			}

			if (c2 != null) {
				Log.e("reconnect", c2.getConnectionStatus() + " " + clientHandleCLOUD);
				if (!c2.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED) && !c2.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTING)) {
					connectMQTT("CLOUD");
				}
			} else {
				connectMQTT("CLOUD");
			}

		}
	}

	public class connectMQTT extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {

			SharedPreferences prefs = getApplicationContext().getSharedPreferences("deviceId", 0);
			String deviceId = prefs.getString("deviceId", "");
			String clientHandleLOCAL = deviceId + ":" + "LOCAL";
			String clientHandleCLOUD = deviceId + ":" + "CLOUD";
			Connection c1 = Connections.getInstance(getApplicationContext()).getConnection(clientHandleLOCAL);
			Connection c2 = Connections.getInstance(getApplicationContext()).getConnection(clientHandleCLOUD);

			if (!c1.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED) && !c1.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTING)) {
				connectMQTT("LOCAL");
			}
			if (!c2.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED) && !c2.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTING)) {
				connectMQTT("CLOUD");

			}

		}

	}

	public class disconnectSuccesfulMQTT extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("disconnect123", "disconnect succesfull");
			if (restartCLOUD) {
				connectMQTT("CLOUD");
				restartCLOUD = false;
			}
			if (restartLOCAL) {
				connectMQTT("LOCAL");
				restartLOCAL = false;
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void notif(String message) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notification_intent = new Intent(getApplicationContext(), MainActivity.class);
		notification_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, notification_intent, 0);
		Notification notification;
		String TITLE = "Messaging Service";
		String TEXT = message;
		if (Build.VERSION.SDK_INT < 11) {
			notification = new Notification(icon, TITLE, when);
			notification.setLatestEventInfo(getApplicationContext(), TITLE, TEXT, pending);
		} else {
			notification = new Notification.Builder(getApplicationContext()).setContentTitle(TITLE).setContentText(TEXT).setSmallIcon(R.drawable.ic_launcher).setContentIntent(pending).setWhen(when).setAutoCancel(true).build();
		}
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		nm.notify(0, notification);

	}

	@SuppressLint("NewApi")
	public class messageArrivedMQTT extends BroadcastReceiver {
		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle intentExtra = intent.getExtras();
			String TOPIC = intentExtra.getString("TOPIC");
			String MESSAGE = intentExtra.getString("MESSAGE");
			String clientHandleReceived = intent.getStringExtra("clientHandle");

			SharedPreferences prefs = getApplicationContext().getSharedPreferences("deviceId", 0);
			String deviceId = prefs.getString("deviceId", "");
			String clientHandleLOCAL = deviceId + ":" + "LOCAL";
			String clientHandleCLOUD = deviceId + ":" + "CLOUD";

			Log.e("messageArrived", TOPIC + " " + clientHandleReceived);

			Connection c = Connections.getInstance(getApplicationContext()).getConnection(clientHandleLOCAL);
			if (c.getConnectionStatus().equals(Connection.ConnectionStatus.CONNECTED) && clientHandleReceived.equals(clientHandleCLOUD)) {
				return;
			}

			if (TOPIC.equals(ID + "/" + "hello")) {
				notif("hello world");
			}

			if (TOPIC.equals(ID + "/" + "response/completeStatus")) {

				try {
					String switchStatusJSON = "statusJSON.cfg";
					FileOutputStream outputStream;
					outputStream = openFileOutput(switchStatusJSON, Context.MODE_PRIVATE);
					outputStream.write(MESSAGE.getBytes());
					outputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction("com.letsandy.updateSwitchView");
				context.sendBroadcast(broadcastIntent);

				Intent broadcastIntent1 = new Intent();
				broadcastIntent1.setAction("com.letsandy.dataSyncReceived");
				sendBroadcast(broadcastIntent1);
			}

			if (TOPIC.equals(ID + "/" + "response/configure")) {

				String STRING_CONFIG = "";
				String STRING_STATUS = "";
				try {
					JSONObject objJSON = new JSONObject(MESSAGE);
					JSONObject CONFIG = objJSON.getJSONObject("MASTER_CFG").getJSONObject("CONFIG");
					JSONObject STATUS = objJSON.getJSONObject("MASTER_CFG").getJSONObject("STATUS");
					STRING_CONFIG = CONFIG.toString();
					STRING_STATUS = STATUS.toString();
				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				try {
					String masterJSON = "masterJSON.cfg";
					FileOutputStream outputStream;
					outputStream = openFileOutput(masterJSON, Context.MODE_PRIVATE);
					outputStream.write(STRING_CONFIG.getBytes());
					outputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					String switchStatusJSON = "statusJSON.cfg";
					FileOutputStream outputStream;
					outputStream = openFileOutput(switchStatusJSON, Context.MODE_PRIVATE);
					outputStream.write(STRING_STATUS.getBytes());
					outputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction("com.letsandy.fullUpdateSwitchView");
				context.sendBroadcast(broadcastIntent);
				Toast.makeText(getApplicationContext(), "Configuration Updated Succesfully", Toast.LENGTH_SHORT).show();

			}

			if (TOPIC.equals(ID + "/" + "response/adminControl")) {
				JSONObject objJSON = null;
				String responseText = "";
				try {
					objJSON = new JSONObject(MESSAGE);
					responseText = objJSON.getString("Response");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (responseText.equals("Learn Completed")) {
					Intent broadcastIntent = new Intent();
					broadcastIntent.setAction("com.letsandy.learnCompleted");
					context.sendBroadcast(broadcastIntent);
				}
			}

			if (TOPIC.equals(ID + "/" + "response/switchControl")) {
				String cur_room = MESSAGE.substring(1, 3);
				String cur_switch = MESSAGE.substring(4, 6);
				String cur_dimmer_status = MESSAGE.substring(7, 9);
				String cur_switch_status = MESSAGE.substring(9, 11);

				String masterJSON = "masterJSON.cfg";
				String cur_switch_type = null;
				try {
					FileInputStream fis = openFileInput(masterJSON);
					InputStreamReader isr = new InputStreamReader(fis);
					BufferedReader bufferedReader = new BufferedReader(isr);
					String rawMasterJSON;
					rawMasterJSON = bufferedReader.readLine();

					JSONObject objJSON = new JSONObject(rawMasterJSON);
					JSONArray arrJSON = objJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getJSONArray("Switch");
					cur_switch_type = arrJSON.getJSONObject(Integer.parseInt(cur_switch)).getString("Type");
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (cur_switch_type.equals("Bell") && cur_switch_status.equals("ON")) {
				}
				try {
					String switchStatusJSON = "statusJSON.cfg";
					FileInputStream fis = openFileInput(switchStatusJSON);
					InputStreamReader isr = new InputStreamReader(fis);
					BufferedReader bufferedReader = new BufferedReader(isr);
					String rawSwitchStatusJSON;
					rawSwitchStatusJSON = bufferedReader.readLine();

					JSONObject objStatusJSON = new JSONObject(rawSwitchStatusJSON);
					JSONArray arrStatusJSON = objStatusJSON.getJSONArray("Room").getJSONObject(Integer.parseInt(cur_room)).getJSONArray("Switch");
					arrStatusJSON.getJSONObject(Integer.parseInt(cur_switch)).remove("SwitchStatus");
					arrStatusJSON.getJSONObject(Integer.parseInt(cur_switch)).put("SwitchStatus", cur_switch_status);

					if (cur_switch_type.equals("Dimmer") || cur_switch_type.equals("Fan")) {
						arrStatusJSON.getJSONObject(Integer.parseInt(cur_switch)).remove("DimmerStatus");
						arrStatusJSON.getJSONObject(Integer.parseInt(cur_switch)).put("DimmerStatus", cur_dimmer_status);
					}

					rawSwitchStatusJSON = objStatusJSON.toString();
					FileOutputStream outputStream = openFileOutput(switchStatusJSON, Context.MODE_PRIVATE);
					outputStream.write(rawSwitchStatusJSON.getBytes());
					outputStream.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction("com.letsandy.updateSwitchView");
				context.sendBroadcast(broadcastIntent);
			}

		}

	}

}