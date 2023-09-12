package com.example.mqtt_brokerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class BrokerListAdapter extends ArrayAdapter<BrokerConfig> {
    private List<BrokerConfig> brokerList;
    private LayoutInflater inflater;


    public BrokerListAdapter(Context context, List<BrokerConfig> brokerList) {
        super(context, 0, brokerList);
        this.brokerList = brokerList;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BrokerConfig broker = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.broker_tile, parent, false);

            TextView textViewName = convertView.findViewById(R.id.tvBrokerName);
            TextView textViewIpAddress = convertView.findViewById(R.id.tvIpAddress);

            textViewName.setText(broker.getHostName());
            textViewIpAddress.setText(broker.getIpAddress());



        }
    return convertView;
    }
}
