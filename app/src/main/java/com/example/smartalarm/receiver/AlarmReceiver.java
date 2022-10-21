package com.example.smartalarm.receiver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartalarm.R;
import com.example.smartalarm.adapter.QuestionAdapter;
import com.example.smartalarm.model.ListQuestion;
import com.example.smartalarm.model.Question;
import com.example.smartalarm.service.AlarmService;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String string = intent.getExtras().getString("extra");
        boolean isStatus = string.equals("on") ? true : false;
        if (!isStatus) {
            return;
        }

        Intent myIntent = new Intent(context, AlarmService.class);
        context.startService(myIntent);
        myIntent.putExtra("extra", "on");
        Toast.makeText(context, "Test Receiver success!", Toast.LENGTH_SHORT).show();
    }
}
