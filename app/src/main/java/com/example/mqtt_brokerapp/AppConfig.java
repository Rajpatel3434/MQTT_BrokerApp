package com.example.mqtt_brokerapp;

public class AppConfig {
    private static AppConfig instance;

    private String ipAddress, topicName, clientName;
    private int port;

    private AppConfig() {
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

    public String getTopicName(){
        return topicName;
    }
    public void setTopicName(String topicName){
        this.topicName = topicName;
    }

    public String getClientName(){
        return clientName;
    }
    public void setClientName(String clientName){
        this.clientName = clientName;
    }
}
