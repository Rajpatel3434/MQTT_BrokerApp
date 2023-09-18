package com.example.mqtt_brokerapp;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttWireMessage;
import org.junit.Before;
import org.junit.Test;

public class MqttDriverActivityTest {

    private MqttDriverActivity mqttDriverActivity;
    private MqttAndroidClient mqttAndroidClient;
    private Switch switchConnect;
    private Button btnSubscribe;
    private Button btnPublish;
    private ImageView connectImg;
    private ImageView disconnectImg;
    private TextView tvStatus;

    @Before
    public void setUp() {
        mqttAndroidClient = mock(MqttAndroidClient.class);
        switchConnect = mock(Switch.class);
        btnSubscribe = mock(Button.class);
        btnPublish = mock(Button.class);
        connectImg = mock(ImageView.class);
        disconnectImg = mock(ImageView.class);
        tvStatus = mock(TextView.class);

        mqttDriverActivity = new MqttDriverActivity(mock(Context.class));
        mqttDriverActivity.mqttAndroidClient = mqttAndroidClient;
        mqttDriverActivity.switchConnect = switchConnect;
        mqttDriverActivity.btnSubscribe = btnSubscribe;
        mqttDriverActivity.btnPublish = btnPublish;
        mqttDriverActivity.connectImg = connectImg;
        mqttDriverActivity.disconnectImg = disconnectImg;
        mqttDriverActivity.tvStatus = tvStatus;
    }

    @Test
    public void testInit() {
        // Act

        // Assert
        verify(switchConnect).setOnCheckedChangeListener(any());
        verify(btnSubscribe).setOnClickListener(any());
        verify(btnPublish).setOnClickListener(any());
        verify(tvStatus).setText("MQTT Message Status");
    }

    @Test
    public void testConnectWTCP() throws MqttException {
        // Act
        mqttDriverActivity.connectWTCP();

        // Assert
        verify(mqttAndroidClient).connect(any(), any());
    }

    @Test
    public void testConnectWSSL() throws MqttException {
        // Arrange
        when(mqttDriverActivity.appConfig.getSslState()).thenReturn(true);

        // Act
        mqttDriverActivity.connectWSSL();

        // Assert
        verify(mqttAndroidClient).connect(any(MqttConnectOptions.class), any());
        verify(mqttAndroidClient).setCallback(any(MqttCallback.class));
    }


    @Test
    public void testSubscribe() throws MqttException {
        // Arrange
        when(mqttDriverActivity.mqttAndroidClient.subscribe(any(), any(), any())).thenReturn(new IMqttToken() {
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
        mqttDriverActivity.mqttSubscribe.subscribe("topic", 0, tvStatus, connectImg, disconnectImg);

        // Assert
        verify(tvStatus).setText("Subscribed to topic");
    }
}