package com.example.mqtt_brokerapp;

import static android.system.Os.connect;
import static kotlinx.coroutines.flow.FlowKt.subscribe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
public class SubScribeActivity extends AppCompatActivity {


    private static final String TAG = "MyTag";
    private Button btnConnect, btnPublish, btnSubscribe;
    private MqttAndroidClient mqttAndroidClient;
    private TextView tvMsg, tvStatus;
    private EditText inputMsg;
    String topic = "mqttHQ-client-test";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_scribe);
        init();
    }

    private void init(){
        btnConnect = findViewById(R.id.btn_connect);
        String serverURL = "tcp://public.mqtthq.com:1883";
        String clientId = "xyz";
        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(),serverURL,clientId);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tvStatus.setText("connect...");
                connectX();
            }
        });

        btnSubscribe = findViewById(R.id.btn_subscribe);

        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: subscribe");
                subscribe();
            }
        });

        tvMsg = findViewById(R.id.tv_msg);
        tvStatus = findViewById(R.id.text);

        inputMsg = findViewById(R.id.edt_input);

    }

    //订阅


    //连接
    private void connectX() {

        try {
            IMqttToken token = mqttAndroidClient.connect();

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(SubScribeActivity.this, "connect onSuccess", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("connect onSuccess");
//                    Log.e(TAG, "connect onSuccess: " + asyncActionToken.getClient().getClientId());
                    subscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

//                        tvStatus.setText("connect onFailure");
                    Log.e(TAG, "connect onFailure: ");
                }
            });
        } catch (MqttException e) {
        }

    }


        private void subscribe() {
        try {
            mqttAndroidClient.subscribe(topic, 0);
            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG, "topic: " + topic);
                    Log.d(TAG, "message: "+ new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

        }
        catch (MqttException e) {
        }

    }

}
