package com.example.smartalarm.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.smartalarm.adapter.ItemTouchHelperListener;
import com.example.smartalarm.R;
import com.example.smartalarm.adapter.RecyclerViewItemTouchHelper;
import com.example.smartalarm.adapter.AlarmAdapter;
import com.example.smartalarm.database.AlarmConverter;
import com.example.smartalarm.database.AlarmDatabase;
import com.example.smartalarm.model.Alarm;
import com.example.smartalarm.receiver.AlarmReceiver;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemTouchHelperListener {

    List<Alarm> listAlarm;
    RecyclerView rcvAlarm;
    AlarmAdapter alarmAdapter;
    LinearLayout layoutRoot;
//    Alarm alarmFirst;

    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init
        layoutRoot = (LinearLayout) findViewById(R.id.layout);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        intent = new Intent(MainActivity.this, AlarmReceiver.class);

        // recycler view
        rcvAlarm = (RecyclerView) findViewById(R.id.recycleViewAlarm);
        listAlarm = new ArrayList<>(getDataAlarm());
        alarmAdapter = new AlarmAdapter(this, listAlarm);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvAlarm.setAdapter(alarmAdapter);
        rcvAlarm.setLayoutManager(linearLayoutManager);

        // swipe to delete
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvAlarm.addItemDecoration(itemDecoration);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rcvAlarm);

//        // drag and drop
//        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        rcvAlarm.addItemDecoration(divider);
//
//        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
//                int positionDragged = dragged.getAdapterPosition();
//                int positionTarget = target.getAdapterPosition();
//                Collections.swap(listAlarm, positionDragged, positionTarget);
//                alarmAdapter.notifyItemMoved(positionDragged, positionTarget);
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder dragged, int direction) {
//
//            }
//        });
//        helper.attachToRecyclerView(rcvAlarm);

        // dialog add
        ImageView imgAdd = (ImageView) findViewById(R.id.imageViewAddAlarm);
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAdd();
            }
        });

        // test broadcast receiver, service
        TextView tvName = (TextView) findViewById(R.id.textViewName);
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendarTest = Calendar.getInstance();
                calendarTest.add(Calendar.MINUTE, 1);
                calendarTest.set(Calendar.SECOND, 0);
                intent.putExtra("extra", "on");
                pendingIntent = PendingIntent.getBroadcast(
                        MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT
                );
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendarTest.getTimeInMillis(), pendingIntent);
                Toast.makeText(MainActivity.this, "Test set alarm success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // share preference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String path = sharedPreferences.getString("Ringtone", "");
        if(!path.isEmpty()) {
            RingtoneManager.getRingtone(this, Uri.parse(path)).play();
        }
    }

    private void showDialogAdd() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_alarm);
        dialog.show();

        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        Button btnSave = dialog.findViewById(R.id.buttonSave);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                addItemNewAlarm(calendar);
                dialog.dismiss();
            }
        });

        // ringtone
//        Spinner spnRingtone = (Spinner) dialog.findViewById(R.id.spinnerRingtone);
//        spnRingtone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDialogRingtone();
//            }
//        });
        TextView tvRingtone = (TextView) dialog.findViewById(R.id.textViewRingtone);
        tvRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogRingtone();
            }
        });
    }

    private void showDialogRingtone() {
//        Intent i = new Intent(this, RingtoneActivity.class);
//        startActivity(i);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_ringtone);
        dialog.show();
    }

    private void addItemNewAlarm(Calendar calendar) {
        // so sanh voi thoi gian hien tai -> giu nguyen/ tang 1 ngay
        Date dateNew = calendar.getTime();
        Calendar calendarCurrent = Calendar.getInstance();
        Date dateCurrent = calendarCurrent.getTime();
        if (dateCurrent.compareTo(dateNew) > 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // kiem tra da ton tai chua
        String stringId = AlarmConverter.fromCalendar(calendar).toString();
        if (!AlarmDatabase.getInstance(this).alarmDAO().checkAlarm(stringId).isEmpty()) {
            return;
        }

        // lay vi tri sau khi chen
        int position = 0;
        for (int i = 0; i < listAlarm.size(); i++) {
            if (listAlarm.get(i).id.compareTo(stringId) > 0) {
                position = i;
                break;
            }
        }

        // add
        Alarm newAlarm = new Alarm(AlarmConverter.fromCalendar(calendar).toString(), calendar, true);
        AlarmDatabase.getInstance(this).alarmDAO().insertAlarm(newAlarm);
        listAlarm.add(position, newAlarm);
        alarmAdapter.notifyDataSetChanged();

    }

    private List<Alarm> getDataAlarm() {
        listAlarm = new ArrayList<>(AlarmDatabase.getInstance(this).alarmDAO().getListAlarm());
        return listAlarm;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof AlarmAdapter.AlarmViewHolder) {
            int indexDelete = viewHolder.getAdapterPosition();
            Alarm alarmDelete = listAlarm.get(indexDelete);

            alarmAdapter.removeItem(alarmDelete, indexDelete);

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