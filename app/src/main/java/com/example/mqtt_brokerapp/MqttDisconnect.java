package com.example.mqtt_brokerapp;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.w3c.dom.Text;

public class MqttDisconnect extends MqttDriverActivity{
    private MqttAndroidClient mqttAndroidClient;

    //MqttDisconnect constructor taking super() method which allows to use all the instances from MqttDriverActivity.
    public MqttDisconnect(MqttAndroidClient mqttAndroidClient) {
        super();
        this.mqttAndroidClient = mqttAndroidClient;
    }

    //disconnect method takes the following paramers and allows to disconnect the service if mqttandroidclient is not null
    public void disconnectX(Switch switchConnect, TextView tvStatus, ImageView connectImg, ImageView disconnectImg) {

        if (mqttAndroidClient != null) {
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
        else {
            // Handle the case where mqttAndroidClient is null (e.g., not connected)
            tvStatus.setText("Not connected");
            switchConnect.setChecked(false);
        }

        }
    }
//
