package com.example.mqtt_brokerapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    String[] permissions = new String[]{
            Manifest.permission.POST_NOTIFICATIONS
    };

    boolean permission_post_notifications = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        btnStart = findViewById(R.id.mainActivity2_startStop_button);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, BrokersListActivity.class);
                if (!permission_post_notifications){
                    requestPermissionNotification();

                }else {
                    startActivity(intent);
                }
            }
        });

    }
//
    public void requestPermissionNotification(){
        if(ContextCompat.checkSelfPermission(MainActivity.this,permissions[0]) == PackageManager.PERMISSION_GRANTED){
            permission_post_notifications = true;
        }
        else {
            if(shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)){
                requestPermissionLauncherNotifications.launch(permissions[0]);
            }
            else{
                requestPermissionLauncherNotifications.launch(permissions[0]);
            }
        }
    }
    private ActivityResultLauncher<String> requestPermissionLauncherNotifications = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted->{
        if(isGranted){
            permission_post_notifications = true;
        }
        else {
            permission_post_notifications = false;
            showPermisisonDialog("Notification Permission");
        }
    });

    public void showPermisisonDialog(String dialogue){
        new AlertDialog.Builder(
                MainActivity.this
        ).setTitle("Permission Alert")
                .setMessage("Please allow notifications in order to run the service in background")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent rintent = new Intent();
                        rintent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",getPackageName(),null);
                        rintent.setData(uri);
                        startActivity(rintent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent( MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}