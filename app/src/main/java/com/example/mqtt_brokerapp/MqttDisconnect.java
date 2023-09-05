package com.example.mqtt_brokerapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.w3c.dom.Text;

public class MqttDisconnect {
    private MqttAndroidClient mqttAndroidClient;
    public MqttDisconnect(MqttAndroidClient mqttAndroidClient) {
        this.mqttAndroidClient = mqttAndroidClient;
    }
    public void disconnectX(Switch switchConnect, TextView tvStatus, ImageView connectImg, ImageView disconnectImg) {
        try {
            IMqttToken disconToken = mqttAndroidClient.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    switchConnect.setActivated(true);
                    tvStatus.setText("Connection Unsuccessful!");
                    connectImg.setVisibility(View.GONE);
                    disconnectImg.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
