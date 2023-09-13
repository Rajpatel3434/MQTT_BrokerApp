package com.example.mqtt_brokerapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.ListView;


import java.lang.reflect.Type;
import java.util.ArrayList;



public class BrokersListActivity extends AppCompatActivity {
    private Button nextBtn;
    private BrokerListAdapter brokerListAdapter;
    private AppConfig appConfig = AppConfig.getInstance();
    ArrayList<BrokerConfig> brokers = new ArrayList<>();
//    final SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
static final String PREFS_NAME = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brokers_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        nextBtn = findViewById(R.id.nextBtnBrokersList);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BrokersListActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Get a reference to your ListView from your XML layout
        ListView brokerListView = findViewById(R.id.brokerListView);

        BrokerConfig broker = new BrokerConfig();
        addBroker(broker);

        brokerListAdapter= new BrokerListAdapter(this, 0,brokers,getSharedPreferences(PREFS_NAME,MODE_PRIVATE));

        brokerListAdapter.notifyDataSetChanged();

        // Set the adapter for your ListView
        brokerListView.setAdapter(brokerListAdapter);

    }
    // Add a broker
    public void addBroker(BrokerConfig brokerConfig) {
        brokers.add(brokerConfig);
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

