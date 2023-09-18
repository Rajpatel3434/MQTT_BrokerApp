package com.example.mqtt_brokerapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
class BrokerListAdapterTest {
    @Test
    public void testGetView() {
        // Create a mock context

        Context mockContext = mock(Context.class);

        // Create a list of broker configs
        List<BrokerConfig> brokerList = new ArrayList<>();
        BrokerConfig brokerConfig = new BrokerConfig();
        brokerConfig.setHostName("My Broker");
        brokerConfig.setIpAddress("127.0.0.1");
        brokerList.add(brokerConfig);

        // Create a mock shared preferences
        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);
        when(mockSharedPreferences.getString("clientNameField", "")).thenReturn("My Client");
        when(mockSharedPreferences.getString("ipAddress", "")).thenReturn("127.0.0.1");

        // Create a new broker list adapter
        BrokerListAdapter brokerListAdapter = new BrokerListAdapter(mockContext, R.layout.broker_tile, brokerList, mockSharedPreferences);

        // Call the getView() method
        View view = brokerListAdapter.getView(0, null, null);

        // Assert that the view is not null
        assertNotNull(view);

        // Assert that the view contains the correct text
        TextView textViewName = view.findViewById(R.id.tvBrokerName);
        assertEquals("Client: My Client", textViewName.getText().toString());

        TextView textViewIpAddress = view.findViewById(R.id.tvIpAddress);
        assertEquals("Host: 127.0.0.1", textViewIpAddress.getText().toString());

        // Assert that the edit button is visible
        Button editBtn = view.findViewById(R.id.btnEdit);
        assertTrue(editBtn.isShown());
    }


}