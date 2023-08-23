package com.example.mqtt_brokerapp;

import static android.system.Os.connect;
import static kotlinx.coroutines.flow.FlowKt.subscribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ComponentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
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
public class MqttConnectionActivity extends AppCompatActivity{


    private static final String TAG = "MyTag";
    private Button btnPublish, btnSubscribe;
    private Switch switchConnect;
    private MqttAndroidClient mqttAndroidClient;
    private TextView tvMsg, tvStatus;
    private EditText inputMsg;
    String topic = "mqttHQ-client-test";
    String serverURL = "tcp://broker.hivemq.com:1883";
    String clientId = "xyz";
     String USERNAME, PASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqttconnection);
        init();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(MqttConnectionActivity.this, MyBackgroundService.class));
        } else {
            startService(new Intent(MqttConnectionActivity.this, MyBackgroundService.class));
        }

        SharedPreferences sp = getApplicationContext().getSharedPreferences("MyUserCreds", Context.MODE_PRIVATE);
        USERNAME= sp.getString("Username","");
        PASSWORD= sp.getString("Password","");

    }

    private void init(){

        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(),serverURL,clientId);

        switchConnect = findViewById(R.id.btn_connect);

        switchConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    tvStatus.setText("Connecting...");

                    connectX();
                } else {
                    disconnectX();
                }
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

        btnPublish = findViewById(R.id.btn_publish);
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish();
            }
        });
    }

    private String messages = null;


    private void connectX() {

        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setAutomaticReconnect(true);

        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), serverURL, clientId);

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "connectionLost: ");
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                String time = System.currentTimeMillis() +"";

                Log.e(TAG, "messageArrived: " + topic + ":" + message.toString());

                String msg = "time: " + time + "\r\n" + "topic: " + topic + "\r\n" + "message: " + message.toString();
                messages = messages == null? msg : messages + "\n" + msg;

                tvMsg.setText( messages );
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

                Log.e(TAG, "deliveryComplete: ");
            }
        });

        try {

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());

            IMqttToken token = mqttAndroidClient.connect(options);

            token.setActionCallback( new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Log.e(TAG, "connect onSuccess: " + asyncActionToken.getClient().getClientId());
                    Toast.makeText(MqttConnectionActivity.this, "connect onSuccess", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Connection Sucessful!");
                    switchConnect.setChecked(true);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    tvStatus.setText("Connection Failed!");
                    Log.e(TAG, "connect onFailure: " );
                    switchConnect.setChecked(false);

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void disconnectX(){
        try {
            IMqttToken disconToken = mqttAndroidClient.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    switchConnect.setActivated(true);
                    tvStatus.setText("Connection Unsuccessful!");
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

        private void subscribe() {
            try {
                mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {

                        Log.e(TAG, "onSuccess: " + asyncActionToken.getClient().getClientId());
                        tvStatus.setText("Subscription Successful!");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                        tvStatus.setText("Subscription Failed!");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
    }

    private void publish() {
        MqttMessage message = new MqttMessage();
        message.setQos(0);
        message.setRetained(false);
        String msg = inputMsg.getText().toString();
        message.setPayload((msg).getBytes());
        try {
            mqttAndroidClient.publish(topic, message, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    tvStatus.setText("Message Published!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    tvStatus.setText("Message Failed!");
                    Log.e(TAG, "onFailure: ");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
