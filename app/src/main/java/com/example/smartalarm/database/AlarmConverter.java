package com.example.smartalarm.database;

import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

public class AlarmConverter {
    @TypeConverter
    public static Calendar toCalendar(Long dateLong){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(dateLong));
        return dateLong == null ? null : calendar;
    }

    @TypeConverter
    public static Long fromCalendar(Calendar calendar){
        return calendar == null ? null : calendar.getTime().getTime();
    }
}
