package com.example.mqtt_brokerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class BrokerListAdapter extends ArrayAdapter<BrokerConfig> {
    private List<BrokerConfig> brokerList;
    private LayoutInflater inflater;

    private Context context;
    private SharedPreferences sharedPreferences;

    public BrokerListAdapter(Context context, int resource,List<BrokerConfig> brokerList, SharedPreferences sharedPreferences) {
        super(context, resource, brokerList);
        this.brokerList = brokerList;
        this.inflater = LayoutInflater.from(context);
        this.sharedPreferences = sharedPreferences;
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.broker_tile, parent, false);

            TextView textViewName = convertView.findViewById(R.id.tvBrokerName);
            TextView textViewIpAddress = convertView.findViewById(R.id.tvIpAddress);
            Button editBtn = convertView.findViewById(R.id.btnEdit);


            String hostName = sharedPreferences.getString("clientNameField","");
            textViewName.setText("Client: "+hostName);

            String ipName = sharedPreferences.getString("ipAddress","");
            textViewIpAddress.setText("Host: "+ipName);

            editBtn.setVisibility(View.VISIBLE);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SettingsActivity.class);
                    context.startActivity(intent);
                }
            });
        }
    return convertView;
    }

}
