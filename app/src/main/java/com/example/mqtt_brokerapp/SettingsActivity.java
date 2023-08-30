package com.example.mqtt_brokerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

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
    private EditText editTextIP, editTextPort, editUserTxt, editPasswordTxt, editTopicTxt, editClientTxt;
    private Button saveBtn;


    private static final String PREFS_NAME = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Toolbar modification
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        init();
        //getting instances ID and assigning
        editTextIP = findViewById(R.id.IPEditTextStngs);
        editTextPort = findViewById(R.id.PortTextStngs);
        editUserTxt = findViewById(R.id.usernameTxtStngs);
        editPasswordTxt = findViewById(R.id.passwordTxtStngs);
        editTopicTxt = findViewById(R.id.topicEditTextStngs);
        editClientTxt = findViewById(R.id.clientEditTextStngs);
        switchSSL = findViewById(R.id.SSLSwitchBtn);
        switchAuthNoAuth = findViewById(R.id.AuthSwitchBtnSetngs);
        saveBtn = findViewById(R.id.SaveBtnStngs);

        final AppConfig appConfig = AppConfig.getInstance();
        final SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedIP = sharedPreferences.getString("ipAddress","");
        String savedUsername = sharedPreferences.getString("usrnameTxtField", "");
        String savedPassword = sharedPreferences.getString("passwordTxtField", "");
        String savedTopic = sharedPreferences.getString("topicNameField","");
        String savedClient = sharedPreferences.getString("clientNameField","");
        int savedPort = sharedPreferences.getInt("port",-1);
        boolean savedSslState = sharedPreferences.getBoolean("sslState", false);
        boolean savedauthNoAuthState = sharedPreferences.getBoolean("authNoAuthState", false);

        editTextIP.setText(savedIP);
        editTextPort.setText(savedPort != -1 ? String.valueOf(savedPort) : "");
        editUserTxt.setText(savedUsername);
        editPasswordTxt.setText(savedPassword);
        editTopicTxt.setText(savedTopic);
        editClientTxt.setText(savedClient);
        switchSSL.setChecked(savedSslState);
        switchAuthNoAuth.setChecked(savedauthNoAuthState);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = editTextIP.getText().toString();
                String portStr = editTextPort.getText().toString();
                String usrnameTxtField = editUserTxt.getText().toString();
                String passwordTxtField= editPasswordTxt.getText().toString();
                String topicNameField = editTopicTxt.getText().toString();
                String clientNameField = editClientTxt.getText().toString();
                boolean sslState = switchSSL.isChecked();
                boolean authNoAuthState = switchAuthNoAuth.isChecked();

                if (portStr.isEmpty() || ipAddress.isEmpty()){
                    Toast.makeText(SettingsActivity.this, "Missing Values!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    if (isNumeric(portStr)){
                        int port = Integer.parseInt(portStr);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("ipAddress", ipAddress);

                        editor.putInt("port", port);
                        editor.putBoolean("sslState", sslState);
                        editor.putBoolean("authNoAuthState",authNoAuthState);
                        editor.putString("topicNameField",topicNameField);
                        editor.putString("clientNameField",clientNameField);
                        if (authNoAuthState) {
                            editor.putString("usrnameTxtField",usrnameTxtField);
                            editor.putString("passwordTxtField",passwordTxtField);
                        } else {
                            editor.remove("usrnameTxtField");
                            editor.remove("passwordTxtField");
                        }
                        editor.apply();
                        appConfig.setIpAddress(ipAddress);
                        appConfig.setPort(port);
                        appConfig.setTopicName(topicNameField);
                        appConfig.setClientName(clientNameField);
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