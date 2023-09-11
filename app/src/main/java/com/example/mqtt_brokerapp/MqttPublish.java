package com.example.mqtt_brokerapp;

import static com.example.mqtt_brokerapp.MqttDriverActivity.TAG;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttPublish extends MqttDriverActivity{


    private MqttAndroidClient mqttAndroidClient;
    public MqttPublish(MqttAndroidClient mqttAndroidClient) {
        super();
        this.mqttAndroidClient = mqttAndroidClient;
    }
    public void publish(int selectedQoS, String topic, boolean isRetained, EditText inputMsg, TextView tvStatus, ImageView connectImg, ImageView disconnectImg) {
        MqttMessage message = new MqttMessage();
        message.setQos(selectedQoS);


        message.setRetained(isRetained);
        String msg = inputMsg.getText().toString();
        message.setPayload((msg).getBytes());
        try {
            mqttAndroidClient.publish(topic, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    tvStatus.setText("Message Published!" );
                    connectImg.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    tvStatus.setText("Message Failed!");
                    Log.e(TAG, "onFailure: ");
                    disconnectImg.setVisibility(View.VISIBLE);

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}