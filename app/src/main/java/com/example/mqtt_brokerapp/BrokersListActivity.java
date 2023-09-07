package com.example.mqtt_brokerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;


public class BrokersListActivity extends AppCompatActivity {
    private Button nextBtn;

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
                Intent intent = new Intent(BrokersListActivity.this, DashBoardActivity.class);
                startActivity(intent);
            }
        });

        ArrayList<AppConfig> brokerConfigList = new ArrayList<>();
        ArrayAdapter<AppConfig> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, brokerConfigList);

        // Get a reference to your ListView from your XML layout
        ListView listView = findViewById(R.id.brokerListView);

    // Set the adapter for your ListView
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppConfig selectedBroker = brokerConfigList.get(position);
            }
        });

    }


}