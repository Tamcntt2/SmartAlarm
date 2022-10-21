package com.example.smartalarm.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.smartalarm.R;
import com.example.smartalarm.ringtone.RingtonePreference;

public class RingtoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone);

        getFragmentManager().beginTransaction().replace(R.id.fragment, new RingtonePreference()).commit();

    }
}