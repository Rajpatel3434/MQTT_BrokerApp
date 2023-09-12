package com.example.mqtt_brokerapp;

import static com.example.mqtt_brokerapp.DashBoardActivity.PREFS_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class BrokersListActivity extends AppCompatActivity {
    private Button nextBtn;
    private BrokerListAdapter brokerListAdapter;
    private AppConfig appConfig = AppConfig.getInstance();
    ArrayList<BrokerConfig> brokers = new ArrayList<>();
    final SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brokers_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
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


        brokerListAdapter= new BrokerListAdapter(this, brokers);

        // Set the adapter for your ListView
        brokerListView.setAdapter(brokerListAdapter);
        brokerListAdapter.notifyDataSetChanged();

        brokerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });

    }


    // Add a broker
    public void addBroker(BrokerConfig brokerConfig) {
        brokers.add(brokerConfig);
    }

}

