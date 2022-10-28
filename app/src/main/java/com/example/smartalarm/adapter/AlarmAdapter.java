package com.example.smartalarm.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartalarm.R;
import com.example.smartalarm.database.AlarmConverter;
import com.example.smartalarm.database.AlarmDatabase;
import com.example.smartalarm.model.Alarm;
import com.example.smartalarm.my_interface.IAlarmManager;
import com.example.smartalarm.util.RingtoneUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private Context context;
    private List<Alarm> listAlarm;
    private IAlarmManager iAlarmManager;
    private RingtoneUtils ringtoneUtils;

    public AlarmAdapter(Context context, IAlarmManager iAlarmManager, RingtoneUtils ringtoneUtils) {
        this.context = context;
        this.listAlarm = new ArrayList<>(AlarmDatabase.getInstance(context).alarmDAO().getListAlarm());
        this.iAlarmManager = iAlarmManager;
        this.ringtoneUtils = ringtoneUtils;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);

        return new AlarmViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = (Alarm) listAlarm.get(position);
        if (alarm == null) return;

        Calendar calendar = AlarmConverter.toCalendar(alarm.getTime());
        SimpleDateFormat formatHour = new SimpleDateFormat("hh:mm aa");
        String hour = formatHour.format(calendar.getTime());
        if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            hour = "00" + hour.substring(2, 8);
        }
        holder.tvHour.setText(hour);

        String[] DAY_OF_WEEKS = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        int idDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String day = DAY_OF_WEEKS[idDay] + ", ";
        SimpleDateFormat formatDay = new SimpleDateFormat("dd-MM");
        day += formatDay.format(calendar.getTime());
        holder.tvDay.setText(day);

        // update isEnabled
        holder.imgStatus.setImageResource(alarm.isEnabled() ? R.drawable.clock_enable : R.drawable.clock_disable);
        holder.imgStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarm.setEnabled(!alarm.isEnabled());
                holder.imgStatus.setImageResource(alarm.isEnabled() ? R.drawable.clock_enable : R.drawable.clock_disable);
                updateIsEnabled(alarm, alarm.isEnabled());
            }
        });

        // update item
        holder.layoutForeground.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                showDialogUpdate(alarm, position);
                Log.d("Alarm id", alarm.getId() + " " + alarm.isEnabled());
            }
        });
    }

    private void updateIsEnabled(Alarm alarm, boolean enabled) {
        int position = findPositionFromIdAlarm(alarm.getId());
        listAlarm.get(position).setEnabled(enabled);

        AlarmDatabase.getInstance(context).alarmDAO().updateAlarm(alarm);

        if (enabled) {
            iAlarmManager.IAddItemAlarmManager(alarm);
        } else {
            iAlarmManager.ICancelItemAlarmManager(alarm);
        }
    }

    private Ringtone ringtoneSelected;
    private RingtoneAdapter ringtoneAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showDialogUpdate(Alarm alarm, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_alarm);
        dialog.show();

        // set timePicker
        TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
        Calendar calendar = AlarmConverter.toCalendar(alarm.getTime());
        timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(calendar.get(Calendar.MINUTE));

        // set spinner
        Spinner spinner = (Spinner) dialog.findViewById(R.id.spinnerRingtone);
        ringtoneAdapter = new RingtoneAdapter(context, R.layout.item_ringtone, ringtoneUtils.getListRingtone());
        spinner.setAdapter(ringtoneAdapter);

        spinner.setSelection(ringtoneUtils.getPosition(alarm.getRingtoneTitle()));
        Log.d("Ringtone", "Position " + ringtoneUtils.getPosition(alarm.getRingtoneTitle()));
        Log.d("Ringtone", "Title " + alarm.getRingtoneTitle());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String notificationTitle = ringtoneAdapter.getItem(i).getTitle();
                String notificationUri = ringtoneUtils.getUriRingtoneFromTitle(notificationTitle);
                if (ringtoneSelected != null && ringtoneSelected.isPlaying()) {
                    ringtoneSelected.stop();
                }
                ringtoneSelected = RingtoneManager.getRingtone(context, Uri.parse(notificationUri));
                ringtoneSelected.play();
                Log.d("Ringtone", notificationTitle + "::: " + notificationUri);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // button save, cancel
        Button btnCancel = dialog.findViewById(R.id.buttonCancel);
        Button btnSave = dialog.findViewById(R.id.buttonSave);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ringtoneSelected.isPlaying()) {
                    ringtoneSelected.stop();
                }
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                Calendar calendarSelected = Calendar.getInstance();
                calendarSelected.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendarSelected.set(Calendar.MINUTE, timePicker.getMinute());
                calendarSelected.set(Calendar.SECOND, 0);
                calendarSelected.set(Calendar.MILLISECOND, 0);

                alarm.setTime(AlarmConverter.fromCalendar(calendarSelected));
                alarm.setEnabled(true);
                updateItem(alarm);
                notifyDataSetChanged();

                if (ringtoneSelected.isPlaying()) {
                    ringtoneSelected.stop();
                }
                dialog.dismiss();
            }
        });
    }

    public void undoItem(Alarm alarm, int position) {
        addItem(alarm);
        notifyItemInserted(position);
    }

    public Alarm getItem(int position) {
        return listAlarm.get(position);
    }


    @Override
    public int getItemCount() {
        if (listAlarm == null) return 0;
        return listAlarm.size();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView tvHour;
        TextView tvDay;
        ImageView imgStatus;
        public RelativeLayout layoutForeground;
        public RelativeLayout layoutBackground;
        Spinner spinner;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);

            tvHour = (TextView) itemView.findViewById(R.id.textViewHour);
            tvDay = (TextView) itemView.findViewById(R.id.textViewDay);
            imgStatus = (ImageView) itemView.findViewById(R.id.imageViewStatus);
            layoutForeground = (RelativeLayout) itemView.findViewById(R.id.layoutForeground);
            layoutBackground = (RelativeLayout) itemView.findViewById(R.id.layoutBackground);
            spinner = (Spinner) itemView.findViewById(R.id.spinnerRingtone);
        }
    }

    private int findPositionFromIdAlarm(int id) {
        int position = -1;
        for (int i = 0; i < listAlarm.size(); i++) {
            if (listAlarm.get(i).getId() == id) {
                position = i;
            }
        }
        return position;
    }

    private Alarm compareTimeCurrent(Alarm alarmNew) {
        // kiem tra -> tang ngay
        Calendar calendarNew = AlarmConverter.toCalendar(alarmNew.getTime());
        Date dateNew = calendarNew.getTime();
        Calendar calendarCurrent = Calendar.getInstance();
        Date dateCurrent = calendarCurrent.getTime();
        if (dateCurrent.compareTo(dateNew) > 0) {
            calendarNew.add(Calendar.DAY_OF_MONTH, 1);
            alarmNew.setTime(AlarmConverter.fromCalendar(calendarNew));
        }

        return alarmNew;
    }

    public void addItem(Alarm alarmNew) {
        alarmNew = compareTimeCurrent(alarmNew);

        // kiem tra ton tai chua
        List<Alarm> listAlarmCheck = AlarmDatabase.getInstance(context).alarmDAO().checkAlarmFromTime(alarmNew.getTime());
        if (listAlarmCheck.isEmpty()) {
            // chua ton tai -> add room, list, alarm manager
            AlarmDatabase.getInstance(context).alarmDAO().insertAlarm(alarmNew);

            listAlarmCheck = AlarmDatabase.getInstance(context).alarmDAO().checkAlarmFromTime(alarmNew.getTime());
            alarmNew.setId(listAlarmCheck.get(0).getId());

            int position = 0;
            for (int i = 0; i < listAlarm.size(); i++) {
                if (listAlarm.get(i).getTime().compareTo(alarmNew.getTime()) < 0) {
                    position = i + 1;
                } else {
                    break;
                }
            }
            listAlarm.add(position, alarmNew);

            iAlarmManager.IAddItemAlarmManager(alarmNew);
        } else {
            // da ton tai Time -> cap nhat room + list + alarm manager
            int position = findPositionFromIdAlarm(listAlarmCheck.get(0).getId());
            Alarm alarmSelected = listAlarm.get(position);
            alarmSelected.setRingtoneTitle(alarmNew.getRingtoneTitle());
            alarmSelected.setEnabled(true);
            listAlarm.set(position, alarmSelected);

            AlarmDatabase.getInstance(context).alarmDAO().updateAlarm(alarmSelected);

            iAlarmManager.IAddItemAlarmManager(listAlarm.get(position));
        }
    }

    public void deleteItem(Alarm alarm) {
        AlarmDatabase.getInstance(context).alarmDAO().deleteAlarm(alarm);

        int position = findPositionFromIdAlarm(alarm.getId());
        if (position != -1) {
            listAlarm.remove(position);
            notifyItemRemoved(position);
        }

        iAlarmManager.ICancelItemAlarmManager(alarm);
    }

    public void updateItem(Alarm alarm) {
        alarm = compareTimeCurrent(alarm);

        // Kiem tra time: da ton tai -> xoa alarm cu
        List<Alarm> listAlarmCheck = AlarmDatabase.getInstance(context).alarmDAO().checkAlarmFromTime(alarm.getTime());
        if (!listAlarmCheck.isEmpty()) {
            deleteItem(listAlarmCheck.get(0));
        }

        // update
        AlarmDatabase.getInstance(context).alarmDAO().updateAlarm(alarm);

        int position = findPositionFromIdAlarm(alarm.getId());
        listAlarm.remove(position);

        int positionNew = 0;
        for (int i = 0; i < listAlarm.size(); i++) {
            if (listAlarm.get(i).getTime().compareTo(alarm.getTime()) < 0) {
                position = i + 1;
            } else {
                break;
            }
        }
        listAlarm.add(position, alarm);

        iAlarmManager.IAddItemAlarmManager(alarm);
    }
}
