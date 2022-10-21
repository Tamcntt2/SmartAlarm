package com.example.smartalarm.model;

import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "alarm")
public class Alarm {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public String id;
    private Calendar calendar;
    private boolean isStatus;
    MediaPlayer mediaPlayer;

    public Alarm(String id, Calendar calendar, boolean isStatus) {
        this.id = id;
        this.calendar = calendar;
        this.isStatus = isStatus;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public boolean isStatus() {
        return isStatus;
    }

    public void setStatus(boolean status) {
        isStatus = status;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
