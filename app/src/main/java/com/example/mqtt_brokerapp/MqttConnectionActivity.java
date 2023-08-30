package com.example.mqtt_brokerapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
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
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class MqttConnectionActivity extends AppCompatActivity {


    private static final String TAG = "MyTag";

    private Button btnPublish, btnSubscribe;
    private Switch switchConnect;
    private MqttAndroidClient mqttAndroidClient;
    private TextView tvMsg, tvStatus;
    private EditText inputMsg;

//    String serverURL = "ssl://broker.hivemq.com:8883";
    //    String serverURL = "tcp://broker.hivemq.com:1883";

    AppConfig appConfig = AppConfig.getInstance();
    String serverURLTCP = "tcp://" + appConfig.getIpAddress() + ":"+ appConfig.getPort();
    String serverURLSSL = "ssl://" + appConfig.getIpAddress() + ":"+ appConfig.getPort();

    String topic = appConfig.getTopicName();
//    String topic = "mqttHQ-client-test";
    String clientId = appConfig.getClientName();
//    String clientId = "xyz";


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

    }

    private static final String PREFS_NAME = "MyPrefs";
    private void init() {

        switchConnect = findViewById(R.id.btn_connect);

        switchConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                boolean authState = sharedPreferences.getBoolean("authNoAuthState", false);
                String username = sharedPreferences.getString("usrnameTxtField", "");
                String password = sharedPreferences.getString("passwordTxtField", "");
                boolean sslState = sharedPreferences.getBoolean("sslState", false);

                if (isChecked  && authState && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    tvStatus.setText("Connecting...");
                    if ((sslState)){
                        Toast.makeText(MqttConnectionActivity.this, "Connection is Encrypted!", Toast.LENGTH_SHORT).show();
                        connectWSSL();
                    } else{
                        connectWTCP();
                    }
                } else {
                    Toast.makeText(MqttConnectionActivity.this, "Disconnected...", Toast.LENGTH_SHORT).show();
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

    private void connectCommon(String serverURL, String username, String password) {
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

                String time = System.currentTimeMillis() + "";

                Log.e(TAG, "messageArrived: " + topic + ":" + message.toString());

                String msg = "time: " + time + "\r\n" + "topic: " + topic + "\r\n" + "message: " + message.toString();
                messages = messages == null ? msg : messages + "\n" + msg;

                tvMsg.setText(messages);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

                Log.e(TAG, "deliveryComplete: ");
            }
        });

        try {

            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());

            IMqttToken token = mqttAndroidClient.connect(options);

            token.setActionCallback( new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Log.e(TAG, "connect onSuccess: " + asyncActionToken.getClient().getClientId());
                    Toast.makeText(MqttConnectionActivity.this, "Connected Successfully!", Toast.LENGTH_SHORT).show();
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

    private void connectWTCP()  {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString("usrnameTxtField", "");
        String password = sharedPreferences.getString("passwordTxtField", "");
        connectCommon(serverURLTCP,username,password);
    }

    private void connectWSSL(){
        // SSL block
        try {

            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient mqttClient = new MqttClient(serverURLSSL, clientId, persistence);

            // Load CA certificate from raw resources
            InputStream caInputStream = getResources().openRawResource(R.raw.ca);
            CertificateFactory caCertFactory = CertificateFactory.getInstance("X.509");
            X509Certificate caCert = (X509Certificate) caCertFactory.generateCertificate(caInputStream);

            // Load client certificate from raw resources
            InputStream clientCertInputStream = getResources().openRawResource(R.raw.clientpem);
            CertificateFactory clientCertFactory = CertificateFactory.getInstance("X.509");
            X509Certificate clientCert = (X509Certificate) clientCertFactory.generateCertificate(clientCertInputStream);

            // Load client private key
            InputStream keyInputStream = getResources().openRawResource(R.raw.clientkey);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", caCert);
            keyStore.setCertificateEntry("client", clientCert);

            // Set up SSL context with client key and certificate
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, null);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            // Configure MQTT connection options
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setSocketFactory(sslContext.getSocketFactory());
            mqttClient.connect(mqttConnectOptions);

        } catch (MqttException e) {
            e.printStackTrace();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString("usrnameTxtField", "");
        String password = sharedPreferences.getString("passwordTxtField", "");
        connectCommon(serverURLSSL,username,password);

    }

    private void disconnectX () {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }else if(id == R.id.action_settings){
            Intent intent = new Intent( MqttConnectionActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
