package com.example.smartalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.smartalarm.service.AlarmService;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isStatus = intent.getBooleanExtra("extra", false);
        String ringtoneTitle = intent.getExtras().getString("ringtone", "Argon");

        Calendar calendar = Calendar.getInstance();
        Log.d("Alarm time", calendar.get(Calendar.HOUR) + "-" + calendar.get(Calendar.MINUTE) + "-" +
                calendar.get(Calendar.SECOND) + "-" + calendar.get(Calendar.MILLISECOND));

        Intent myIntent = new Intent(context, AlarmService.class);
        myIntent.putExtra("extra", isStatus);
        myIntent.putExtra("ringtone", ringtoneTitle);
        context.startService(myIntent);
    }
}
