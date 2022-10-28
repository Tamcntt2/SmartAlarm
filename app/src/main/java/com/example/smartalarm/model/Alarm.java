package com.example.smartalarm.model;

import android.media.MediaPlayer;
import android.media.Ringtone;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.smartalarm.database.AlarmConverter;

import java.util.Calendar;

@Entity(tableName = "alarm")
public class Alarm {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String time;
    private boolean isEnabled;
    private String ringtoneTitle;

    public Alarm(String time, boolean isEnabled, String ringtoneTitle) {
        this.time = time;
        this.isEnabled = isEnabled;
        this.ringtoneTitle = ringtoneTitle;
    }

    public Alarm() {
        this.isEnabled = true;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getRingtoneTitle() {
        return ringtoneTitle;
    }

    public void setRingtoneTitle(String ringtoneTitle) {
        this.ringtoneTitle = ringtoneTitle;
    }
}
