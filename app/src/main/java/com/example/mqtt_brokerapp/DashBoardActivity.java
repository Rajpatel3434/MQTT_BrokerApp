package com.example.mqtt_brokerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;

public class DashBoardActivity extends AppCompatActivity {
    public SharedPreferences sharedPreferences;
    Button stopBrokerBtn;
    Button startBrokerBtn;
    private Button subBtn;
    TextView tView1;
    TextView ipAdd;
    private TextView ipAddTv;
    TextView portTextView;
    TextView authTextView;
    TextView connectionTypeTextView;
    WifiManager wifiManager;
    static final String PREFS_NAME = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        Log.d("APP","OnCreate");

        // Background service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, MyBackgroundService.class));
        } else {
            startService(new Intent(this, MyBackgroundService.class));
        }
        setContentView(R.layout.activity_dash_board);

        //Toolbar design and creation
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        //Wifi manager which helps to get IP address
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

//calling methods setInstances() which allows to gather IDs and view information on the dashboard window and refreshButtons() allows to refresh buttons while switching between the activities;
        setInstances();
        refreshButtons();
    }
    private void setInstances(){
        //setup all the instances and store using sharedpreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        //IP address / Host view
        ipAdd = findViewById(R.id.startSignView);
        ipAddTv = findViewById(R.id.ipTv);
        String ipAddress = sharedPreferences.getString("ipAddress", "");
        ipAddTv.setText(ipAddress);

        //port number view
        int portNumber = sharedPreferences.getInt("port", -1);
        portTextView = findViewById(R.id.portTextView);

        if (portNumber != 0) {
            portTextView.setText(portNumber != -1 ? String.valueOf(portNumber) : "");
        } else {
            portTextView.setText("");
        }

        //Authenticaiton boolean view yes or no
        authTextView = findViewById(R.id.authTextView);
        boolean authTextViewBool = sharedPreferences.getBoolean("authNoAuthState", false);

        if (authTextViewBool) {
            authTextView.setText("Yes");
        } else {
            authTextView.setText("No");
        }

        //Connection type view (SSl or TCP)
        connectionTypeTextView = findViewById(R.id.connectionTypeTextView);
        boolean connectionTypeBool = sharedPreferences.getBoolean("sslState", false);

        if (connectionTypeBool) {
            connectionTypeTextView.setText("SSL");
        } else {
            connectionTypeTextView.setText("TCP");
        }

        //terminal textview with initial text "mqtt is not running"
        tView1 = findViewById(R.id.stoppedSignView);

        //Buttons find and view and what happens once pressed.
        stopBrokerBtn = findViewById(R.id.brokerStopBtn);
        subBtn = findViewById(R.id.subBtn);
        stopBrokerBtn.setOnClickListener(v -> stopBrokerService());
        startBrokerBtn = findViewById(R.id.brokerStartBtn);
        startBrokerBtn.setOnClickListener(v -> startBrokerService());

        subBtn.setOnClickListener(v -> openSettingsActivity());
    }

    //onresume method which allows to run refresh button when resuming the current activity
    @Override
    protected void onResume() {
        super.onResume();
        refreshButtons();
    }

    //stopbroker button service which allows to stop background services
    void stopBrokerService() {
        stopService(new Intent(DashBoardActivity.this, MyBackgroundService.class));
        startBrokerBtn.setEnabled(true);
        stopBrokerBtn.setEnabled(false);
        tView1.setText("mqtt> Server is not running");
        ipAdd.setText("");
    }

    //startbroker button service which allows to start background services
    void startBrokerService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(DashBoardActivity.this, MyBackgroundService.class));
        } else {
            startService(new Intent(DashBoardActivity.this, MyBackgroundService.class));
        }
        startBrokerBtn.setEnabled(false);
        stopBrokerBtn.setEnabled(true);
        String display = "mqtt> Server is started... ";
        tView1.setText(display);
        ipAdd.setText("mqtt> IP: " + Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        ipAdd.setEnabled(true);
        Toast.makeText(DashBoardActivity.this, "Server started...", Toast.LENGTH_SHORT).show();
    }

    //settings activity opens up once settings buttons is pressed
    void openSettingsActivity() {
        Intent intent = new Intent(DashBoardActivity.this, BrokersListActivity.class);
        startActivity(intent);
    }

    //refresh buttons which allows to run start and stop buttons between activies switched
    void refreshButtons() {
        startBrokerBtn.setEnabled(!MyBackgroundService.isRunning());
        stopBrokerBtn.setEnabled(MyBackgroundService.isRunning());
        String display = "mqtt> Server is started... ";
        tView1.setText(display);
        ipAdd.setText("mqtt> IP: " + Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
    }

    //oncreatemenu which allows to create settings button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return super.onCreateOptionsMenu(menu);
    }

    //onoptionsitemselected method which allows to click on the settings page
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            openSettingsActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
