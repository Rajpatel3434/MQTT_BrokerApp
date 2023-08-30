package com.example.mqtt_brokerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class DashBoardActivity extends AppCompatActivity {
    private Button stopbrokerbtn, startbrokerbtn, subbtn;
    private TextView tView1, tView2, ipadd, ipaddtv;
     EditText usernameTxt, passwordTxt;
    private ConstraintLayout authCL;
    private RadioButton rbAuth, rbNoAuth;
    String USERNAME, PASSWORD;

    SharedPreferences sp;
    private static final String PREFS_NAME = "MyPrefs";
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

        //ip address when start button is pressed
        ipadd = (TextView) findViewById(R.id.startSignView);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        //ip address for setting up text view for the display part
        ipaddtv = (TextView) findViewById(R.id.ipTv);
        ipaddtv.setText( Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));

        tView1 = (TextView) findViewById(R.id.stoppedSignView);
        stopbrokerbtn = findViewById(R.id.brokerStopBtn);

        subbtn = findViewById(R.id.subBtn);
        subbtn.setEnabled(false);
        //on pressing stop button shows server is stopped
        stopbrokerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(DashBoardActivity.this, MyBackgroundService.class));
                // finish();
                startbrokerbtn.setEnabled(true);
                stopbrokerbtn.setEnabled(false);
                subbtn.setEnabled(false);
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
                subbtn.setEnabled(true);
                String display = "mqtt> Server is started... ";
                tView1.setText(display);
                ipadd.setText( "mqtt> IP: "+Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
                ipadd.setEnabled(true);
                Toast.makeText(DashBoardActivity.this, "Server started...", Toast.LENGTH_SHORT).show();
            }
        });

        // Ask user for the username & password by getting their IDs
        usernameTxt = findViewById(R.id.usernameTxtStngs);
        passwordTxt = findViewById(R.id.passwordTxtStngs);
        String usrnameTxtField = usernameTxt.getText().toString();
        String passwordTxtField= passwordTxt.getText().toString();

        //Shared preferences where next activity can use the objects (username & password)
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString("usrnameTxtField", "");
        String password = sharedPreferences.getString("passwordTxtField", "");

        subbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, MqttConnectionActivity.class);
                startActivity(intent);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", username);
                editor.putString("password",password);
                editor.commit();

            }
        });

        //on pressing Authentication radio button opens up Textview with username and password
        rbAuth = findViewById(R.id.AuthRadioBtn);
        rbNoAuth = findViewById(R.id.noAuthRadioBtn);


        authCL = findViewById(R.id.authConstraintlayout);
        authCL.setVisibility(View.GONE);


        rbAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (authCL.getVisibility() == View.GONE){
                    authCL.setVisibility(View.VISIBLE);
                }

            }
        });

        rbNoAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authCL.getVisibility() == View.VISIBLE){
                    authCL.setVisibility(View.GONE);
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
            Intent intent = new Intent( DashBoardActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
