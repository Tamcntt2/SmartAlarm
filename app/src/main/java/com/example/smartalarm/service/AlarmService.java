package com.example.smartalarm.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.smartalarm.R;
import com.example.smartalarm.activity.QuestionActivity;
import com.example.smartalarm.database.AlarmDatabase;
import com.example.smartalarm.model.Alarm;
import com.example.smartalarm.util.RingtoneUtils;

public class AlarmService extends Service {

    private boolean isStatus;
    Ringtone ringtone;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isStatus = intent.getBooleanExtra("extra", false);
        String ringtoneTitle = intent.getExtras().getString("ringtone", "Argon");
        int idAlarm = intent.getIntExtra("idAlarm", -1);

        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }

        if (isStatus) {
            RingtoneUtils ringtoneUtils = new RingtoneUtils(getApplicationContext());
            String notificationUri = ringtoneUtils.getUriRingtoneFromTitle(ringtoneTitle);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(notificationUri));
//            ringtone.setLooping(true);
            ringtone.play();

            Intent intent1 = new Intent(this, QuestionActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("idAlarm", idAlarm);
            startActivity(intent1);
        }

        return START_NOT_STICKY;
    }
}
