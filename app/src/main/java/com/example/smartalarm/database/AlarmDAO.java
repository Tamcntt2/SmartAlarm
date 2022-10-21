package com.example.smartalarm.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartalarm.model.Alarm;

import java.util.Calendar;
import java.util.List;

@Dao
public interface AlarmDAO {
    @Insert
    void insertAlarm(Alarm alarm);

    @Query("SELECT * FROM alarm ORDER BY id")
    List<Alarm> getListAlarm();

    @Query("SELECT * FROM alarm WHERE id= :id")
    List<Alarm> checkAlarm(String id);

    @Delete
    void deleteAlarm(Alarm alarm);

    @Update
    void updateAlarm(Alarm alarm);
}
