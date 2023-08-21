package com.example.mqtt_brokerapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.MenuCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.graphics.drawable.ColorDrawable;
import android.widget.RadioButton;
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

public class DashBoardActivity extends AppCompatActivity {
    private Button stopbrokerbtn, startbrokerbtn, subbtn;
    private TextView tView1, tView2, ipadd, ipaddtv, authTextview;
    private RadioButton rbAuth, rbNoAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("APP","OnCreate");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, MyBackgroundService.class));
        } else {
            startService(new Intent(this, MyBackgroundService.class));
        }


        setContentView(R.layout.activity_dash_board);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));

//        ColorDrawable colorDrawable
//                = new ColorDrawable(getResources().getColor(R.color.white));
//
//        // Set BackgroundDrawable
//        myToolbar.setNavigationIcon(colorDrawable);

        //ip address when start button is pressed
        ipadd = (TextView) findViewById(R.id.startSignView);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        //ip address for setting up text view for the display part
        ipaddtv = (TextView) findViewById(R.id.ipTv);
        ipaddtv.setText( Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));

        tView1 = (TextView) findViewById(R.id.stoppedSignView);
        stopbrokerbtn = findViewById(R.id.brokerStopBtn);

        //on pressing stop button shows server is stopped
        stopbrokerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(DashBoardActivity.this, MyBackgroundService.class));
                // finish();
                startbrokerbtn.setEnabled(true);
                stopbrokerbtn.setEnabled(false);
                String display = "mqtt> Server is stopped...";
                tView1.setText(display);
                ipadd.setText("");
            }
        });

        //on pressing start button shows server is started
        startbrokerbtn = findViewById(R.id.brokerStartBtn);
        startbrokerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(new Intent(DashBoardActivity.this, MyBackgroundService.class));
                } else {
                    startService(new Intent(DashBoardActivity.this, MyBackgroundService.class));
                }
                startbrokerbtn.setEnabled(false);
                stopbrokerbtn.setEnabled(true);

                String display = "mqtt> Server is started... ";
                tView1.setText(display);
                ipadd.setText( "mqtt> IP: "+Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
                ipadd.setEnabled(true);
                Toast.makeText(DashBoardActivity.this, "Server started...", Toast.LENGTH_SHORT).show();

            }
        });

        subbtn = findViewById(R.id.subBtn);
        subbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DashBoardActivity.this, MqttConnectionActivity.class);
                startActivity(intent);
            }
        });



        //on pressing Authentication radio button opens up Textview with username and password
        rbAuth = findViewById(R.id.AuthRadioBtn);
        rbNoAuth = findViewById(R.id.noAuthRadioBtn);


        authTextview = findViewById(R.id.authTv);
        authTextview.setVisibility(View.GONE);
        rbAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (authTextview.getVisibility() == View.GONE){
                    authTextview.setVisibility(View.VISIBLE);
                }
            }
        });

        rbNoAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authTextview.getVisibility() == View.VISIBLE){
                    authTextview.setVisibility(View.GONE);
                }
            }
        });


        this.refreshButtons();

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.refreshButtons();
    }

    private void refreshButtons() {
        startbrokerbtn.setEnabled( !MyBackgroundService.isRunning() );
        stopbrokerbtn.setEnabled( MyBackgroundService.isRunning() );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return super.onCreateOptionsMenu(menu);
    }


    ///Back button in the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if(id == R.id.action_settings){
            Intent intent = new Intent( DashBoardActivity.this, MqttConnectionActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
