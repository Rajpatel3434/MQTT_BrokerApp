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

//    List<MqttMessage> getAllMessages();

        @Query("SELECT * FROM mqttmessageconfig")
        List<MqttMessageConfig> getAllMessages();
//
//        @Query("SELECT * FROM MqttMessage WHERE topic = :topic")
//        List<MqttMessage> getByTopic(String topic);
//
//        @Query("UPDATE MqttMessage SET state = :state WHERE id = :id")
//        void updateState(int id, String state);
//
//        @Delete
//        void delete(MqttMessage message);
    }


