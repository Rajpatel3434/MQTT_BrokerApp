package com.example.mqtt_brokerapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class MqttDriverActivity extends AppCompatActivity {


    static final String TAG = "MyTag";

    private Button btnPublish, btnSubscribe, btnDone;
    private Switch switchConnect, switchRetained;
    private MqttAndroidClient mqttAndroidClient;
    private TextView tvMsg, tvStatus;
    private EditText inputMsg;
    private ImageView connectImg, disconnectImg;

    private AppConfig appConfig = AppConfig.getInstance();
    private String serverURLTCP = "tcp://" + appConfig.getIpAddress() + ":"+ appConfig.getPort();
    private String serverURLSSL = "ssl://" + appConfig.getIpAddress() + ":"+ appConfig.getPort();

    private String topic = appConfig.getTopicName();
    private String clientId = appConfig.getClientName();
    private String username = appConfig.getUserNameTxt();
    private String password = appConfig.getPasswordTxt();

    private int selectedQoS = 0;
    private boolean isRetained = false;

    private Context context;
    public MqttDriverActivity(Context context){
        this.context = context;
    }

    public MqttDriverActivity() {

    }

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
            startForegroundService(new Intent(MqttDriverActivity.this, MyBackgroundService.class));
        } else {
            startService(new Intent(MqttDriverActivity.this, MyBackgroundService.class));
        }

    }

    private static final String PREFS_NAME = "MyPrefs";
    private void init() {


        switchConnect = findViewById(R.id.btn_connect);

        switchConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                boolean sslState = appConfig.getSslState();
                if (isChecked) {
                    tvStatus.setText("Connecting...");
                    if (sslState){
                        Toast.makeText(MqttDriverActivity.this, "Connection is Encrypted!", Toast.LENGTH_SHORT).show();
                        connectWSSL();
                    } else{
                        connectWTCP();
                    }
                } else {
                    Toast.makeText(MqttDriverActivity.this, "Disconnected...", Toast.LENGTH_SHORT).show();
                    MqttDisconnect mqttDisconnect = new MqttDisconnect(mqttAndroidClient);
                    mqttDisconnect.disconnectX(switchConnect,tvStatus,connectImg,disconnectImg);
                }
            }
        });

        btnSubscribe = findViewById(R.id.btn_subscribe);

        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: subscribe");
                MqttSubscribe mqttSubscribe = new MqttSubscribe(mqttAndroidClient);
                mqttSubscribe.subscribe(topic,selectedQoS,tvStatus, connectImg, disconnectImg);
            }
        });

        tvMsg = findViewById(R.id.tv_msg);
        tvStatus = findViewById(R.id.textStatus);

        inputMsg = findViewById(R.id.edt_input);

        btnPublish = findViewById(R.id.btn_publish);
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MqttPublish mqttPublish = new MqttPublish(mqttAndroidClient);
                mqttPublish.publish(selectedQoS,topic,isRetained,inputMsg,tvStatus,connectImg,disconnectImg);
            }
        });
        tvMsg = findViewById(R.id.tv_msg);
        tvStatus = findViewById(R.id.textStatus);
        btnDone = findViewById(R.id.doneBtnMqtt);


        final SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedtextStatus = sharedPreferences.getString("textStatus","MQTT Message Status");
        String savedtvMsg = sharedPreferences.getString("tvMsg","");
        boolean savedBtnConnectState = sharedPreferences.getBoolean("btnConnect",false);
        boolean savedBtnSubscribeState = sharedPreferences.getBoolean("btnSubscribe", false);
        boolean savedBtnPublishState = sharedPreferences.getBoolean("btnPublish", false);


        tvStatus.setText(savedtextStatus);
        tvMsg.setText(savedtvMsg);
        switchConnect.setChecked(savedBtnConnectState);
        btnSubscribe.setActivated(savedBtnSubscribeState);
        btnPublish.setActivated(savedBtnPublishState);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textStatus = tvStatus.getText().toString();
                String textMsg = tvMsg.getText().toString();
                boolean btnConnect = switchConnect.isChecked();
                boolean btnSubscribed = btnSubscribe.isActivated();
                boolean btnPublished = btnPublish.isActivated();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("textStatus",textStatus);
//
                editor.putBoolean("btnConnect",btnConnect);
                editor.putBoolean("btnSubscribe",btnSubscribed);
                editor.putBoolean("btnPublish",btnPublished);
                editor.putString("tvMsg",textMsg);
                editor.apply();
                launchDashboardActivity();
                finish();

            }
        });

        Spinner qosSpinner = findViewById(R.id.qosSpinnerId);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.QoS_numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        qosSpinner.setAdapter(adapter);
        qosSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedQosString = parentView.getItemAtPosition(position).toString();
                selectedQoS = extractQoSValue(selectedQosString);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        switchRetained = findViewById(R.id.retainSwitchId);
        switchRetained.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isRetained = true;
                } else{
                    isRetained = false;
                }
            }
        });

        connectImg = findViewById(R.id.connectedImageView);
        disconnectImg = findViewById(R.id.disconnectedImageView);
        connectImg.setVisibility(View.GONE);
        disconnectImg.setVisibility(View.GONE);

    }
    private int extractQoSValue(String qosString) {
        switch (qosString) {
            case "0":
                return 0;
            case "1":
                return 1;
            case "2":
                return 2;
            default:
                return 0; // Default to QoS 0 if not recognized
        }
    }
    private void launchDashboardActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String messages = null;

    private void connectCommon(String serverURL, String username, String password)   {
        this.username = username;
        this.password = password;
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setAutomaticReconnect(true);

        connectImg = findViewById(R.id.connectedImageView);
        disconnectImg = findViewById(R.id.disconnectedImageView);
        connectImg.setVisibility(View.GONE);
        disconnectImg.setVisibility(View.GONE);
        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), serverURL, clientId);

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG, "connectionLost: ");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                String time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

                Log.e(TAG, "messageArrived: " + topic + ":" + message.toString());

                String msg = "time: " + time + "\r\n" + "topic: " + "\r\n" + "QoS: " + selectedQoS + "\r\n" + "message: " + message.toString();
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
                    Toast.makeText(MqttDriverActivity.this, "Connected Successfully!", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Connection Sucessful!");
                    switchConnect.setChecked(true);
                    connectImg.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    tvStatus.setText("Connection Failed!");
                    Log.e(TAG, "connect onFailure: " );
                    switchConnect.setChecked(false);
                    disconnectImg.setVisibility(View.VISIBLE);

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void connectWTCP()  {

        connectCommon(serverURLTCP,username,password);
    }

    private void connectWSSL(){
        // SSL block
        try {

            String caCertificateUriString = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("caCertificateUri", null);
            String clientCertificateUriString = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("clientCertificateUri", null);

            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient mqttClient = new MqttClient(serverURLSSL, clientId, persistence);

            // Load CA certificate from raw resources
//            InputStream caInputStream = getResources().openRawResource(R.raw.ca);
            InputStream caInputStream = new FileInputStream(caCertificateUriString);
            CertificateFactory caCertFactory = CertificateFactory.getInstance("X.509");
            X509Certificate caCert = (X509Certificate) caCertFactory.generateCertificate(caInputStream);

            // Load client certificate from raw resources
//            InputStream clientCertInputStream = getResources().openRawResource(R.raw.clientpem);
            InputStream clientCertInputStream = new FileInputStream(clientCertificateUriString);
            CertificateFactory clientCertFactory = CertificateFactory.getInstance("X.509");
            X509Certificate clientCert = (X509Certificate) clientCertFactory.generateCertificate(clientCertInputStream);

            // Load client private key
//            InputStream keyInputStream = getResources().openRawResource(R.raw.clientkey);
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
        connectCommon(serverURLSSL,username,password);
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
            Intent intent = new Intent( MqttDriverActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
