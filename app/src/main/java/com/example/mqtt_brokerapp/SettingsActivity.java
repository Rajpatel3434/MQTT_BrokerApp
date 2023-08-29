package com.example.mqtt_brokerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchSSL, switchAuthNoAuth;
    private RadioButton crtRadioBtn;
    private ConstraintLayout authCL;
    private EditText editTextIP, editTextPort;
    private Button saveBtn;

    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        init();

        editTextIP = findViewById(R.id.IPEditTextStngs);
        editTextPort = findViewById(R.id.PortTextStngs);
        saveBtn = findViewById(R.id.SaveBtnStngs);

        final AppConfig appConfig = AppConfig.getInstance();
        final SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedIP = sharedPreferences.getString("ipAddress","");
        int savedPort = sharedPreferences.getInt("port",-1);

        editTextIP.setText(savedIP);
        editTextPort.setText(savedPort != -1 ? String.valueOf(savedPort) : "");
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = editTextIP.getText().toString();
                String portStr = editTextPort.getText().toString();
                if (portStr.isEmpty()){
                    Toast.makeText(SettingsActivity.this, "Empty Field!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    if (isNumeric(portStr)){
                        int port = Integer.parseInt(portStr);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("ipAddress", ipAddress);
                        editor.putInt("port", port);
                        editor.apply();

                        appConfig.setIpAddress(ipAddress);
                        appConfig.setPort(port);
                        launchDashboardActivity();
                        finish();
                    } else{
                        Toast.makeText(SettingsActivity.this, "Port number must contain digits", Toast.LENGTH_SHORT).show();
                    }
             
                }

            }
        });
    }
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private void launchDashboardActivity() {
        Intent intent = new Intent(this, MqttConnectionActivity.class);
        startActivity(intent);
        finish();
    }

    private void init(){
        authCL = findViewById(R.id.authConstraintlayout);
        authCL.setVisibility(View.GONE);

        switchAuthNoAuth = findViewById(R.id.AuthSwitchBtnSetngs);
        switchAuthNoAuth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (authCL.getVisibility() == View.GONE){
                        authCL.setVisibility(View.VISIBLE);
                    }

                } else{
                    authCL.setVisibility(View.GONE);
                }
            }
        });


        switchSSL = findViewById(R.id.SSLSwitchBtn);
        crtRadioBtn = findViewById(R.id.crtRadioBtn);
        crtRadioBtn.setVisibility(View.GONE);
        switchSSL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (crtRadioBtn.getVisibility() == View.GONE){
                        crtRadioBtn.setVisibility(View.VISIBLE);
                    }
                }else {
                    crtRadioBtn.setVisibility(View.GONE);
                }
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}