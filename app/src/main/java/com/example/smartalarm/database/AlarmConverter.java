package com.example.smartalarm.database;

import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

public class AlarmConverter {
    @TypeConverter
    public static Calendar toCalendar(String stringDate){
        Calendar calendar = Calendar.getInstance();
        long intDate = Long.parseLong(stringDate);
        calendar.setTime(new Date(intDate));
        return stringDate == null ? null : calendar;
    }

    @TypeConverter
    public static String fromCalendar(Calendar calendar){
        return String.valueOf(calendar == null ? null : calendar.getTime().getTime());
    }
}
