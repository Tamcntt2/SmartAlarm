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

    @Query("SELECT * FROM alarm ORDER BY time")
    List<Alarm> getListAlarm();

    @Query("SELECT * FROM alarm WHERE time= :time")
    List<Alarm> checkAlarmFromTime(String time);

    @Query("SELECT * FROM alarm WHERE id= :id")
    List<Alarm> checkAlarmFromId(int id);

    @Delete
    void deleteAlarm(Alarm alarm);

    @Update
    void updateAlarm(Alarm alarm);
}
