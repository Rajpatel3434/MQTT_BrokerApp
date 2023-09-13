package com.example.mqtt_brokerapp;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttSubscribe extends MqttDriverActivity{

    private MqttAndroidClient mqttAndroidClient;

    public MqttSubscribe(MqttAndroidClient mqttAndroidClient) {
        super();
        this.mqttAndroidClient = mqttAndroidClient;
    }
    public void subscribe(String topic, int selectedQoS, TextView tvStatus, ImageView connectImg, ImageView disconnectImg) {

        try {
            mqttAndroidClient.subscribe(topic, selectedQoS, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Log.e(TAG, "onSuccess: " + asyncActionToken.getClient().getClientId());
                    tvStatus.setText("Subscription Successful!");
                    connectImg.setVisibility(View.VISIBLE);

                }
//
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    tvStatus.setText("Subscription Failed!");
                    disconnectImg.setVisibility(View.VISIBLE);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
