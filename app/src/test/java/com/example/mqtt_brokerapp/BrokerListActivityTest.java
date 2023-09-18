package com.example.mqtt_brokerapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.MenuItem;

import org.junit.Test;

public class BrokerListActivityTest {
    BrokersListActivity brokersListActivity = new BrokersListActivity();
    BrokerConfig brokerConfig = new BrokerConfig();

    @Test
    public void testAddBroker() {
        // Create a new broker config

        // Add the broker config to the list of brokers

        brokersListActivity.addBroker(brokerConfig);

        // Assert that the broker config is in the list of brokers
        assertEquals(1, brokersListActivity.brokers.size());
        assertTrue(brokersListActivity.brokers.contains(brokerConfig));
    }

    @Test
    public void testOnOptionsItemSelected() {
        // Create a new brokers list activity

//        // Create a mock menu item
        MenuItem menuItem = mock(MenuItem.class);
        when(menuItem.getItemId()).thenReturn(android.R.id.home);

        // Call the onOptionsItemSelected() method
        boolean result = brokersListActivity.onOptionsItemSelected(menuItem);

//        // Assert that the onBackPressed() method was called
        verify(brokersListActivity).onBackPressed();

        // Assert that the result is true
        assertTrue(result);
    }


}
