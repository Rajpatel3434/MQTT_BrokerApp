package com.example.mqtt_brokerapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DashBoardActivityTest {

    @Mock
    private DashBoardActivity dashBoardActivity;

    @Mock
    private WifiManager wifiManager;

    @Mock
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        dashBoardActivity = new DashBoardActivity();
        dashBoardActivity.wifiManager = wifiManager;
        dashBoardActivity.sharedPreferences = sharedPreferences;
    }

    @Test
    public void testOnCreate() {
        // Arrange
        when(sharedPreferences.getString("ipAddress", "")).thenReturn("127.0.0.1");
        when(sharedPreferences.getInt("port", -1)).thenReturn(1883);
        when(sharedPreferences.getBoolean("authNoAuthState", false)).thenReturn(false);
        when(sharedPreferences.getBoolean("sslState", false)).thenReturn(false);

        // Act
        dashBoardActivity.onCreate(null);

        // Assert
        assertEquals("127.0.0.1", dashBoardActivity.ipAdd.getText().toString());
        assertEquals("1883", dashBoardActivity.portTextView.getText().toString());
        assertEquals("No", dashBoardActivity.authTextView.getText().toString());
        assertEquals("TCP", dashBoardActivity.connectionTypeTextView.getText().toString());
        assertTrue(dashBoardActivity.startBrokerBtn.isEnabled());
        assertFalse(dashBoardActivity.stopBrokerBtn.isEnabled());
        assertEquals("mqtt> Server is not running", dashBoardActivity.tView1.getText().toString());
    }

    @Test
    public void testStartBrokerService() {
        // Arrange

        // Act
        dashBoardActivity.startBrokerService();

        // Assert
        assertFalse(dashBoardActivity.startBrokerBtn.isEnabled());
        assertTrue(dashBoardActivity.stopBrokerBtn.isEnabled());
        assertEquals("mqtt> Server is started... ", dashBoardActivity.tView1.getText().toString());
        assertEquals("mqtt> IP: 127.0.0.1", dashBoardActivity.ipAdd.getText().toString());
    }

    @Test
    public void testStopBrokerService() {
        // Arrange

        // Act
        dashBoardActivity.stopBrokerService();

        // Assert
        assertTrue(dashBoardActivity.startBrokerBtn.isEnabled());
        assertFalse(dashBoardActivity.stopBrokerBtn.isEnabled());
        assertEquals("mqtt> Server is not running", dashBoardActivity.tView1.getText().toString());
        assertEquals("", dashBoardActivity.ipAdd.getText().toString());
    }

    @Test
    public void testOpenSettingsActivity() {
        // Arrange

        // Act
        dashBoardActivity.openSettingsActivity();

        // Assert
        Intent expectedIntent = new Intent(dashBoardActivity, BrokersListActivity.class);
        Intent actualIntent = dashBoardActivity.getIntent();
        assertEquals(expectedIntent, actualIntent);
    }

    @Test
    public void testRefreshButtons() {
        // Arrange
        dashBoardActivity.startBrokerService();

        // Act
        dashBoardActivity.refreshButtons();

        // Assert
        assertFalse(dashBoardActivity.startBrokerBtn.isEnabled());
        assertTrue(dashBoardActivity.stopBrokerBtn.isEnabled());
        assertEquals("mqtt> Server is started... ", dashBoardActivity.tView1.getText().toString());
        assertEquals("mqtt> IP: 127.0.0.1", dashBoardActivity.ipAdd.getText().toString());
    }
}
