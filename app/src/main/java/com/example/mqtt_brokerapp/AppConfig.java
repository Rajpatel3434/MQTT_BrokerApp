package com.example.mqtt_brokerapp;

import java.io.Serializable;

public class AppConfig implements Serializable {
    private static AppConfig instance;

    private String ipAddress, topicName, clientName, userNameTxt, passwordTxt ;
    private boolean authNoAuthState, sslState;
    private int port;

    AppConfig() {
    }
//

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
    public String getUserNameTxt(){return userNameTxt;}
    public void setUserNameTxt(String userNameTxt){this.userNameTxt = userNameTxt;}

    public void setPasswordTxt(String passwordTxt){this.passwordTxt = passwordTxt;}
    public String getPasswordTxt() { return passwordTxt;}

    public void setAuthNoAuthState(boolean authNoAuthState){this.authNoAuthState = authNoAuthState;}
    public boolean getAuthNoAuthState(){return authNoAuthState;}

    public void setSslState(boolean sslState){this.sslState = sslState;}
    public boolean getSslState(){return sslState;}

}
