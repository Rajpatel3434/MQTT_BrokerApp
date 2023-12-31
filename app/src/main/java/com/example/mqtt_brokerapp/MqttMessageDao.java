package com.example.mqtt_brokerapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MqttMessageDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(MqttMessageConfig mqttMessageConfig);

        @Query("SELECT * FROM mqttmessageconfig")
        List<MqttMessageConfig> getAllMessages();

        @Query("DELETE FROM mqttmessageconfig")
        void deleteAll();

    }


