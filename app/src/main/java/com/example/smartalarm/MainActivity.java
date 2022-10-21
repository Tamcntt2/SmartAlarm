package com.example.smartalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.smartalarm.adapter.AlarmAdapter;
import com.example.smartalarm.database.AlarmConverter;
import com.example.smartalarm.database.AlarmDatabase;
import com.example.smartalarm.model.Alarm;
import com.example.smartalarm.receiver.AlarmReceiver;
import com.google.android.material.snackbar.Snackbar;

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
    }

    private void addItemNewAlarm(Calendar calendar) {
        // so sanh voi thoi gian hien tai
        Date dateNew = calendar.getTime();
        Calendar calendarCurrent = Calendar.getInstance();
        Date dateCurrent = calendarCurrent.getTime();
        if (dateCurrent.compareTo(dateNew) > 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // lay vi tri sau khi sap xep va kiem tra xem no co ton tai chua
        // get position after sorting
        int pos = 0;
        boolean check = true;
        for (int i = 0; i < listAlarm.size(); i++) {
            Calendar calendar1 = listAlarm.get(i).getCalendar();
            Date date1 = calendar1.getTime();
            dateNew = calendar.getTime();
            if (date1.compareTo(dateNew) < 0) {
                pos = i + 1;
            } else if (date1.compareTo(dateNew) == 0) {
                pos = i;
                check = false;
                break;
            }
        }

        Alarm newAlarm = new Alarm(AlarmConverter.fromCalendar(calendar).toString(), calendar, true);
        if (check) {
            listAlarm.add(pos, newAlarm);
            // insert room
            AlarmDatabase.getInstance(this).alarmDAO().insertAlarm(newAlarm);
        }
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

            alarmAdapter.removeItem(indexDelete, alarmDelete);

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