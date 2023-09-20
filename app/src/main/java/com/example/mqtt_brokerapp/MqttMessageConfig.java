package com.example.mqtt_brokerapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MqttMessageConfig {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String topic;
    private String payload;
    private long timestamp;
    private int state;


    public MqttMessageConfig(String topic, String payload, long timestamp, int state) {
        this.topic = topic;
        this.payload = payload;
        this.timestamp = timestamp;
        this.state = state;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
