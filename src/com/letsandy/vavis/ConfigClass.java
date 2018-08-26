package com.letsandy.vavis;

public class ConfigClass {
	protected static final int CONN_TIMEOUT = 60;
	protected static final int CONN_KEEPALIVE = 60;
	protected static final int BROKER_PORT = 1883;
	protected static final String CLOUD_BROKER_ADDRESS = "mqtt.letsandy.com";
	protected static final String[] TOPICS = { "hello", "response/switchControl", "response/completeStatus", "response/configure", "response/adminControl" };
	static String CLOUD_BROKER_URI = "tcp://mqtt.letsandy.com:1883";
}
