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

// Object initiation start //
    static final String TAG = "MyTag";

    private Button btnPublish, btnSubscribe, btnDone;
    private Switch switchConnect, switchRetained;
    private MqttAndroidClient mqttAndroidClient;
    private TextView tvMsg, tvStatus;
    private EditText inputMsg;
    private ImageView connectImg, disconnectImg;

    //Taking instances (setters and getters) from AppConfig class
    private AppConfig appConfig = AppConfig.getInstance();
    private String serverURLTCP = "tcp://" + appConfig.getIpAddress() + ":"+ appConfig.getPort();
    private String serverURLSSL = "ssl://" + appConfig.getIpAddress() + ":"+ appConfig.getPort();

    private String topic = appConfig.getTopicName();
    private String clientId = appConfig.getClientName();
    private String username = appConfig.getUserNameTxt();
    private String password = appConfig.getPasswordTxt();
    private int selectedQoS = 0;
    private boolean isRetained = false;
    private Spinner qosSpinner;

    private Context context;
    private static final String PREFS_NAME = "MyPrefs";

    // Object initiation stop //

    public MqttDriverActivity(Context context){
        this.context = context;
    }

    public MqttDriverActivity() {

    }

    //oncreate allows init() method, and keeps continuing background service
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqttconnection);
        init();

        //Toolbar creation and design
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

        btnSubscribe.setEnabled(false);
        btnPublish.setEnabled(false);
    }

    // init() method allowing multiple buttons to design and implementing on changing the state
    private void init() {
        // switch connect button implementation where on successful connection allow the service to connect otherwise get the state to the original

        switchConnect = findViewById(R.id.btn_connect);
        switchConnect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // choose whether to connect over ssl or tcp
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
                    mqttDisconnect.disconnectX(switchConnect,tvStatus,connectImg,disconnectImg); // disconnect if connection is a failure!
                }
            }
        });

        //subscribe button which allows to subscribe the client

        btnSubscribe = findViewById(R.id.btn_subscribe);
        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: subscribe");
                MqttSubscribe mqttSubscribe = new MqttSubscribe(mqttAndroidClient);
                mqttSubscribe.subscribe(topic,selectedQoS,tvStatus, connectImg, disconnectImg); // subscribe using subsribe class
            }
        });


        tvMsg = findViewById(R.id.tv_msg);
        tvStatus = findViewById(R.id.textStatus);

        inputMsg = findViewById(R.id.edt_input);

        //publish button where user can publish message to the broker using mqttPublish class

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
        switchRetained = findViewById(R.id.retainSwitchId);
        qosSpinner = findViewById(R.id.qosSpinnerId);


        // Shared preferences which allows to store data instances and later can be used by other Activities
        final SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedtextStatus = sharedPreferences.getString("textStatus","MQTT Message Status");
        String savedtvMsg = sharedPreferences.getString("tvMsg","");
        boolean savedBtnConnectState = sharedPreferences.getBoolean("btnConnect",false);
        boolean savedBtnSubscribeState = sharedPreferences.getBoolean("btnSubscribe", false);
        boolean savedBtnPublishState = sharedPreferences.getBoolean("btnPublish", false);
        boolean savedBtnRetainState = sharedPreferences.getBoolean("btnRetain",false);
        boolean savedQoSState = sharedPreferences.getBoolean("textQos",false);

        // save the saved objects to the Views objects / casting the object views to the Views
        tvStatus.setText(savedtextStatus);
        tvMsg.setText(savedtvMsg);
        switchConnect.setChecked(savedBtnConnectState);
        btnSubscribe.setActivated(savedBtnSubscribeState);
        btnPublish.setActivated(savedBtnPublishState);
        switchRetained.setChecked(savedBtnRetainState);
        qosSpinner.setActivated(savedQoSState);

        //Done button implementation which allows to store the data instaces such as the state of the buttons, terminal messages etc.
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textStatus = tvStatus.getText().toString();
                String textMsg = tvMsg.getText().toString();
                boolean btnConnect = switchConnect.isChecked();
                boolean btnSubscribed = btnSubscribe.isActivated();
                boolean btnPublished = btnPublish.isActivated();
                boolean btnRetain = switchRetained.isChecked();
                boolean textQos = qosSpinner.isActivated();
                SharedPreferences.Editor editor = sharedPreferences.edit(); // editor allows to put objects and store
                editor.putString("textStatus",textStatus);
                editor.putBoolean("btnConnect",btnConnect);
                editor.putBoolean("btnSubscribe",btnSubscribed);
                editor.putBoolean("btnPublish",btnPublished);
                editor.putString("tvMsg",textMsg);
                editor.putBoolean("btnRetain",btnRetain);
                editor.putBoolean("textQos", textQos);
                editor.apply();
                launchDashboardActivity(); // launchdashboardactivity once Done button is pressed.
                finish();

            }
        });

        dropDownQoSSpinner();
        retainMessages();
        connectImg = findViewById(R.id.connectedImageView);
        disconnectImg = findViewById(R.id.disconnectedImageView);
        connectImg.setVisibility(View.GONE);
        disconnectImg.setVisibility(View.GONE);
    }

    //dropDownQoSSpinner allows to choose QoS for the messages to publish
    private void dropDownQoSSpinner(){
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

    }

    //retain switch implementation whether to retain messages once subscribed to the service
    private void retainMessages(){
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
    }
    //get the values either 0,1 or 2 for QoS
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
        onBackPressed();
        finish();
    }

    private String messages = null;

    //connect Common method which takes common url, username and password
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

        //on mqttAndroidClient callback allow to connect wait for message to arrive and set time, topic, QoS and message.
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
        // set username and password if user clicks on the Authentication button
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(username);
            options.setPassword(password.toCharArray());

            IMqttToken token = mqttAndroidClient.connect(options);

            token.setActionCallback( new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                //on success allow the text to be changed including switch state and subsribe and publish buttons
                    Log.e(TAG, "connect onSuccess: " + asyncActionToken.getClient().getClientId());
                    Toast.makeText(MqttDriverActivity.this, "Connected Successfully!", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Connection Sucessful!");
                    switchConnect.setChecked(true);
                    connectImg.setVisibility(View.VISIBLE);
                    btnSubscribe.setEnabled(true);
                    btnPublish.setEnabled(true);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    tvStatus.setText("Connection Failed!");
                    Log.e(TAG, "connect onFailure: " );
                    switchConnect.setChecked(false);
                    disconnectImg.setVisibility(View.VISIBLE);
                    btnSubscribe.setEnabled(false);
                    btnPublish.setEnabled(false);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // follow commonConnect method for TCP approach but only changing common serverURL to serverURLTCP
    private void connectWTCP()  {

        connectCommon(serverURLTCP,username,password);
    }

    // follow commonConnect method for SSL approach but only changing common serverURL to serverSSL

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

 // onCreateOptionsMenu allows to show back arrow
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return super.onCreateOptionsMenu(menu);
    }

    // implement onCreateOptinsMenu design showcase on onOptionsItemSelected method allowing to open settings and backpressed activity and method.
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
