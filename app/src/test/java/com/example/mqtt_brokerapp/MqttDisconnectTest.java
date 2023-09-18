package com.example.mqtt_brokerapp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttWireMessage;
import org.junit.Before;
import org.junit.Test;

public class MqttDisconnectTest {

    private MqttDisconnect mqttDisconnect;
    private MqttAndroidClient mqttAndroidClient;
    private Switch switchConnect;
    private TextView tvStatus;
    private ImageView connectImg;
    private ImageView disconnectImg;

    @Before
    public void setUp() {
        mqttAndroidClient = mock(MqttAndroidClient.class);
        switchConnect = mock(Switch.class);
        tvStatus = mock(TextView.class);
        connectImg = mock(ImageView.class);
        disconnectImg = mock(ImageView.class);

        mqttDisconnect = new MqttDisconnect(mqttAndroidClient);
    }

    @Test
    public void testDisconnectX() throws MqttException {
        // Arrange
        when(mqttAndroidClient.disconnect()).thenReturn(new IMqttToken() {
            @Override
            public void waitForCompletion() throws MqttException {

            }

            @Override
            public void waitForCompletion(long timeout) throws MqttException {

            }

            @Override
            public boolean isComplete() {
                return false;
            }

            @Override
            public MqttException getException() {
                return null;
            }

            @Override
            public void setActionCallback(IMqttActionListener actionListener) {
                // Act
                actionListener.onSuccess(null);
            }

            @Override
            public IMqttActionListener getActionCallback() {
                return null;
            }

            @Override
            public IMqttAsyncClient getClient() {
                return null;
            }

            @Override
            public String[] getTopics() {
                return new String[0];
            }

            @Override
            public void setUserContext(Object userContext) {

            }

            @Override
            public Object getUserContext() {
                return null;
            }

            @Override
            public int getMessageId() {
                return 0;
            }

            @Override
            public int[] getGrantedQos() {
                return new int[0];
            }

            @Override
            public boolean getSessionPresent() {
                return false;
            }

            @Override
            public MqttWireMessage getResponse() {
                return null;
            }
        });

        // Act
        mqttDisconnect.disconnectX(switchConnect, tvStatus, connectImg, disconnectImg);

        // Assert
        verify(switchConnect).setActivated(true);
        verify(tvStatus).setText("Connection Unsuccessful!");
        verify(connectImg).setVisibility(View.GONE);
        verify(disconnectImg).setVisibility(View.VISIBLE);
    }

    @Test
    public void testDisconnectXWhenMqttAndroidClientIsNull() {
        // Arrange
        mqttAndroidClient = null;

        // Act
        mqttDisconnect.disconnectX(switchConnect, tvStatus, connectImg, disconnectImg);

        // Assert
        verify(tvStatus).setText("Not connected");
        verify(switchConnect).setChecked(false);
    }
}
