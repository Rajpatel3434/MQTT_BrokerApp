package com.example.mqtt_brokerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchSSL, switchAuthNoAuth;
    private ConstraintLayout authCL, crtCL;
    private EditText editTextIP, editTextPort, editUserTxt, editPasswordTxt, editTopicTxt, editClientTxt;
    private Button saveBtn;
    private ImageButton caCrtBtn, clientCrtBtn;
    private TextView caFileTv;
    private TextView clientFileTv;

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
         caCrtBtn = findViewById(R.id.caFileBtn);
         clientCrtBtn = findViewById(R.id.clientFileBtn);
        caFileTv = findViewById(R.id.caFileTextView);
        clientFileTv = findViewById(R.id.clientFileTextView);
        crtCL = findViewById(R.id.certficateConstraintLayout);

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

        init();

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
        String savedcaFileTv = sharedPreferences.getString("caFileTvField","CA Certificate");
        String savedclientFileTv = sharedPreferences.getString("clientFileTvField","Client Certificate");


        editTextIP.setText(savedIP);
        editTextPort.setText((savedPort != -1 ? String.valueOf(savedPort) : ""));
        editUserTxt.setText(savedUsername);
        editPasswordTxt.setText(savedPassword);
        editTopicTxt.setText(savedTopic);
        editClientTxt.setText(savedClient);
        switchSSL.setChecked(savedSslState);
        switchAuthNoAuth.setChecked(savedauthNoAuthState);

        caFileTv.setText(savedcaFileTv);
        clientFileTv.setText(savedclientFileTv);
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

                String caFileTvField = caFileTv.getText().toString();
                String clientFileTvField = clientFileTv.getText().toString();

                SharedPreferences.Editor editor = sharedPreferences.edit();


                if (portStr.isEmpty() || ipAddress.isEmpty() || topicNameField.isEmpty() || clientNameField.isEmpty()||(authNoAuthState && (usrnameTxtField.isEmpty() && passwordTxtField.isEmpty()))){
                    Toast.makeText(SettingsActivity.this, "Missing Values!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    if (isNumeric(portStr) ){
                        int port = Integer.parseInt(editTextPort.getText().toString());
                        editor.putString("ipAddress",ipAddress);
                        editor.putInt("port",port);
                        editor.putString("usrnameTxtField",usrnameTxtField);
                        editor.putString("passwordTxtField",passwordTxtField);
                        editor.putString("topicNameField",topicNameField);
                        editor.putString("clientNameField",clientNameField);
                        editor.putBoolean("sslState",sslState);
                        editor.putBoolean("authNoAuthState",authNoAuthState);
                        editor.putString("caFileTvField",caFileTvField);
                        editor.putString("clientFileTvField",clientFileTvField);
                        editor.apply();
                        appConfig.setIpAddress(ipAddress);
                        appConfig.setPort(port);
                        appConfig.setTopicName(topicNameField);
                        appConfig.setClientName(clientNameField);
                        appConfig.setUserNameTxt(usrnameTxtField);
                        appConfig.setPasswordTxt(passwordTxtField);
                        appConfig.setAuthNoAuthState(authNoAuthState);
                        appConfig.setSslState(sslState);
                        launchDashboardActivity();
                        finish();
                    } else{
                        Toast.makeText(SettingsActivity.this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this,MqttDriverActivity.class);
        startActivity(intent);
        finish();
    }

    private void init() {
        authCL = findViewById(R.id.authConstraintlayout);
        authCL.setVisibility(View.GONE);

        switchAuthNoAuth = findViewById(R.id.AuthSwitchBtnSetngs);
        switchAuthNoAuth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (authCL.getVisibility() == View.GONE) {
                        authCL.setVisibility(View.VISIBLE);
                    }

                } else {
                    authCL.setVisibility(View.GONE);
                }
            }
        });


        switchSSL = findViewById(R.id.SSLSwitchBtn);

        crtCL.setVisibility(View.GONE);
        switchSSL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (crtCL.getVisibility() == View.GONE) {
                        crtCL.setVisibility(View.VISIBLE);
                    }
                } else {
                    crtCL.setVisibility(View.GONE);
                }
            }
        });



        caCrtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCaCrtFileChooser();

            }
        });
        clientCrtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClientCrtFileChooser();
            }
        });
    }

    private void showCaCrtFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a CA Certificate file"),101);
        } catch (Exception e){
            Toast.makeText(this, "You need a file manager!", Toast.LENGTH_SHORT).show();
        }
    }
    private void showClientCrtFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a Client Certificate file"),102);
        } catch (Exception e){
            Toast.makeText(this, "You need a file manager!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 101 && resultCode == RESULT_OK && data !=null){
            Uri caCertificateUri = data.getData();
            String caCertpath = caCertificateUri.getPath();
            File cafile = new File(caCertpath);
            saveFileUriToSharedPreferences("caCertificateUri", caCertificateUri);

            caFileTv.setText("CA cert " +cafile + " " + "File name: " + cafile.getName());
            try {
                // Open an InputStream for the selected CA certificate file
                InputStream inputStream = getContentResolver().openInputStream(caCertificateUri);

                // Now you can use the inputStream to read the content of the CA certificate file.
                // For example, you can create a byte array to read the content:
                byte[] buffer = new byte[1024];
                int bytesRead;
                StringBuilder content = new StringBuilder();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    content.append(new String(buffer, 0, bytesRead));
                }

                // You can now use the 'content' StringBuilder to access the content of the CA certificate file.

                // Don't forget to close the InputStream when you're done with it.
                inputStream.close();

            } catch (Exception e) {
                // Handle any exceptions that may occur while reading the file.
                e.printStackTrace();
            }

        } else if (requestCode == 102 && resultCode == RESULT_OK && data !=null){
            Uri clientCertificateUri = data.getData();
            String clientCertpath = clientCertificateUri.getPath();
            File clientfile = new File(clientCertpath);
            saveFileUriToSharedPreferences("clientCertificateUri", clientCertificateUri);
            clientFileTv.setText("Client cert " +clientCertpath + " " + "File name: " + clientfile.getName());
            try {
                // Open an InputStream for the selected CA certificate file
                InputStream inputStream = getContentResolver().openInputStream(clientCertificateUri);

                // Now you can use the inputStream to read the content of the CA certificate file.
                // For example, you can create a byte array to read the content:
                byte[] buffer = new byte[1024];
                int bytesRead;
                StringBuilder content = new StringBuilder();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    content.append(new String(buffer, 0, bytesRead));
                }

                // You can now use the 'content' StringBuilder to access the content of the CA certificate file.
                // Don't forget to close the InputStream when you're done with it.
                inputStream.close();

            } catch (Exception e) {
                // Handle any exceptions that may occur while reading the file.
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // Helper method to save a file URI to SharedPreferences
    private void saveFileUriToSharedPreferences(String key, Uri fileUri) {
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        editor.putString(key, fileUri.toString());
        editor.apply();
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