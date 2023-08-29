package com.example.mqtt_brokerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchSSL, switchAuthNoAuth;
    private RadioButton crtRadioBtn;

    private ConstraintLayout authCL;
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