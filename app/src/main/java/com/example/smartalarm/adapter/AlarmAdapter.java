package com.example.smartalarm.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartalarm.R;
import com.example.smartalarm.database.AlarmConverter;
import com.example.smartalarm.database.AlarmDatabase;
import com.example.smartalarm.model.Alarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    Context context;
    List<Alarm> listAlarm;

    public AlarmAdapter(Context context, List<Alarm> listAlarm) {
        this.context = context;
        this.listAlarm = listAlarm;
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

        Calendar calendar = alarm.getCalendar();
        SimpleDateFormat formatHour = new SimpleDateFormat("hh:mm aa");
        String hour = formatHour.format(calendar.getTime());
        if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            hour = "00" + hour.substring(2, 8);
        }
        holder.tvHour.setText(hour);
//        holder.tvHour.setText(calendar.get(Calendar.HOUR_OF_DAY) + "");

        String[] DAY_OF_WEEKS = {"Chủ nhật", "Thứ hai", "Thứ ba", "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy"};
        int idDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String day = DAY_OF_WEEKS[idDay] + ", ";
        SimpleDateFormat formatDay = new SimpleDateFormat("dd-MM");
        day += formatDay.format(calendar.getTime());
        holder.tvDay.setText(day);

        // update status
        holder.imgStatus.setImageResource(alarm.isStatus() ? R.drawable.clock_enable : R.drawable.clock_disable);
        holder.imgStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarm.setStatus(!alarm.isStatus());
                holder.imgStatus.setImageResource(alarm.isStatus() ? R.drawable.clock_enable : R.drawable.clock_disable);
                updateItem(alarm, position);
            }
        });

        // update item
        holder.layoutForeground.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                showDialogUpdate(alarm, position);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showDialogUpdate(Alarm alarm, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_alarm);
        dialog.show();

        // set timePicker
        TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
        Calendar calendar = alarm.getCalendar();
        timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(calendar.get(Calendar.MINUTE));

        // button save, cancel
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
                Calendar calendarNew = Calendar.getInstance();
                calendarNew.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendarNew.set(Calendar.MINUTE, timePicker.getMinute());
                Alarm alarmSelected = new Alarm(AlarmConverter.fromCalendar(calendarNew).toString(), calendarNew, true);
                updateItem(alarmSelected, position);
                dialog.dismiss();
            }
        });
    }

    private void updateItem(Alarm alarm, int position) {
        removeItem(alarm, position);
        addItem(alarm);
    }

    private void addItem(Alarm alarm) {
        // so sanh voi thoi gian hien tai -> tang ngay
        Calendar calendar = alarm.getCalendar();
        Date dateNew = calendar.getTime();
        Calendar calendarCurrent = Calendar.getInstance();
        Date dateCurrent = calendarCurrent.getTime();
        if (dateCurrent.compareTo(dateNew) > 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // kiem tra da ton tai chua
        String stringId = AlarmConverter.fromCalendar(calendar).toString();
        if (!AlarmDatabase.getInstance(context).alarmDAO().checkAlarm(stringId).isEmpty()) {
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
        AlarmDatabase.getInstance(context).alarmDAO().insertAlarm(newAlarm);
        listAlarm.add(position, newAlarm);


    }

    public void removeItem(Alarm alarm, int position) {
        listAlarm.remove(position);
        AlarmDatabase.getInstance(context).alarmDAO().deleteAlarm(alarm);
        notifyItemRemoved(position);
    }

    private List<Alarm> getDataAlarm() {
        return AlarmDatabase.getInstance(context).alarmDAO().getListAlarm();
    }

    public void undoItem(Alarm alarm, int position) {
        listAlarm.add(position, alarm);
        notifyItemInserted(position);
        AlarmDatabase.getInstance(context).alarmDAO().insertAlarm(alarm);
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

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);

            tvHour = (TextView) itemView.findViewById(R.id.textViewHour);
            tvDay = (TextView) itemView.findViewById(R.id.textViewDay);
            imgStatus = (ImageView) itemView.findViewById(R.id.imageViewStatus);
            layoutForeground = (RelativeLayout) itemView.findViewById(R.id.layoutForeground);
            layoutBackground = (RelativeLayout) itemView.findViewById(R.id.layoutBackground);
        }
    }
}
