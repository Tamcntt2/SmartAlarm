package com.example.smartalarm.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.smartalarm.adapter.RingtoneAdapter;
import com.example.smartalarm.my_interface.IAlarmManager;
import com.example.smartalarm.my_interface.ItemTouchHelperListener;
import com.example.smartalarm.R;
import com.example.smartalarm.adapter.RecyclerViewItemTouchHelper;
import com.example.smartalarm.adapter.AlarmAdapter;
import com.example.smartalarm.database.AlarmConverter;
import com.example.smartalarm.model.Alarm;
import com.example.smartalarm.receiver.AlarmReceiver;
import com.example.smartalarm.util.RingtoneUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements ItemTouchHelperListener {

    private RecyclerView rcvAlarm;
    private AlarmAdapter alarmAdapter;
    private LinearLayout layoutRoot;
    private AlarmManager alarmManager;
    private Intent intent;
    private RingtoneAdapter ringtoneAdapter;
    private RingtoneUtils ringtoneUtils;

    private IAlarmManager iAlarmManager = new IAlarmManager() {
        @Override
        public void IAddItemAlarmManager(Alarm alarm) {
            Calendar calendar = AlarmConverter.toCalendar(alarm.getTime());
            intent.putExtra("extra", true);
            intent.putExtra("ringtone", alarm.getRingtoneTitle());
            intent.putExtra("idAlarm", alarm.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    MainActivity.this, alarm.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        @Override
        public void ICancelItemAlarmManager(Alarm alarm) {
            alarmManager.cancel(PendingIntent.getBroadcast(
                    MainActivity.this, alarm.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
            ));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init
        layoutRoot = (LinearLayout) findViewById(R.id.layout);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        intent = new Intent(MainActivity.this, AlarmReceiver.class);
        ringtoneUtils = new RingtoneUtils(this);

        // recycler view
        rcvAlarm = (RecyclerView) findViewById(R.id.recycleViewAlarm);
        alarmAdapter = new AlarmAdapter(this, iAlarmManager, ringtoneUtils);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvAlarm.setAdapter(alarmAdapter);
        rcvAlarm.setLayoutManager(linearLayoutManager);

        // swipe to delete
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvAlarm.addItemDecoration(itemDecoration);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rcvAlarm);

        // dialog add
        ImageView imgAdd = (ImageView) findViewById(R.id.imageViewAddAlarm);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAdd();
            }
        });
    }

    private Ringtone ringtoneSelected;
    private void showDialogAdd() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_alarm);
        dialog.show();

        Alarm alarmSelected = new Alarm();

        // spinner ringtone
        Spinner spinner = (Spinner) dialog.findViewById(R.id.spinnerRingtone);
        ringtoneAdapter = new RingtoneAdapter(getApplicationContext(), R.layout.item_ringtone, ringtoneUtils.getListRingtone());
        spinner.setAdapter(ringtoneAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String notificationTitle = ringtoneAdapter.getItem(i).getTitle();
                String notificationUri = ringtoneUtils.getUriRingtoneFromTitle(notificationTitle);
                if (ringtoneSelected != null && ringtoneSelected.isPlaying()) {
                    ringtoneSelected.stop();
                }
                ringtoneSelected = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(notificationUri));
                ringtoneSelected.play();
                Log.d("Ringtone", notificationTitle + "::: " + notificationUri);
                alarmSelected.setRingtoneTitle(notificationTitle);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // button save and cancel
        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        Button btnSave = dialog.findViewById(R.id.buttonSave);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ringtoneSelected.isPlaying()) {
                    ringtoneSelected.stop();
                }
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
                Calendar calendarSelected = Calendar.getInstance();
                calendarSelected.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendarSelected.set(Calendar.MINUTE, timePicker.getMinute());
                calendarSelected.set(Calendar.SECOND, 0);
                calendarSelected.set(Calendar.MILLISECOND, 0);
                alarmSelected.setTime(AlarmConverter.fromCalendar(calendarSelected));

                alarmAdapter.addItem(alarmSelected);
                alarmAdapter.notifyDataSetChanged();

                if(ringtoneSelected.isPlaying()) {
                    ringtoneSelected.stop();
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof AlarmAdapter.AlarmViewHolder) {
            int indexDelete = viewHolder.getAdapterPosition();
            Alarm alarmDelete = alarmAdapter.getItem(indexDelete);

            alarmAdapter.deleteItem(alarmDelete);
            alarmAdapter.notifyDataSetChanged();

            Snackbar snackbar = Snackbar.make(layoutRoot, "", Snackbar.LENGTH_SHORT);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alarmAdapter.undoItem(alarmDelete, indexDelete);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}