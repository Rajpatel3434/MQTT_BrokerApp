package com.example.mqtt_brokerapp;
import android.content.Context;
import android.util.Log;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTConnection {

        private static final String TAG = "MqttManager";
        private MqttAndroidClient mqttClient;

        public void connect(Context context) {
            String serverURI = "tcp://172.16.0.202";
            mqttClient = new MqttAndroidClient(context, serverURI, "java_client");
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    Log.d(TAG, "Receive message: " + message.toString() + " from topic: " + topic);
                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG, "Connection lost " + cause.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

            MqttConnectOptions options = new MqttConnectOptions();
            try {
                mqttClient.connect(options, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d(TAG, "Connection success");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d(TAG, "Connection failure");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }


}


