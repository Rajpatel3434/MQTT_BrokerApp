package com.example.mqtt_brokerapp;

import java.io.Serializable;

public class BrokerConfig implements Serializable {
    private String ipAddress;
    private String hostName;
    private AppConfig appConfig = AppConfig.getInstance();

    public BrokerConfig(){
    }
//
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getHostName() {
        return appConfig.getClientName();
    }

    public String getIpAddress() {
        return appConfig.getIpAddress();
    }
}
