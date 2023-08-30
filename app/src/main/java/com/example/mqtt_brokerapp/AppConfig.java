package com.example.mqtt_brokerapp;

public class AppConfig {
    private static AppConfig instance;

    private String ipAddress;
    private int port;

    private AppConfig() {
        // Private constructor to prevent external instantiation
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}