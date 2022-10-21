package com.example.smartalarm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartalarm.R;
import com.example.smartalarm.model.Alarm;
import com.example.smartalarm.model.Question;
import com.example.smartalarm.service.AlarmService;

import java.util.List;

public class QuestionAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<String> listAnswers;
    private String rightAnswer;

    public QuestionAdapter(Context context, int layout, List<String> listAnswers, String rightAnswer) {
        this.context = context;
        this.layout = layout;
        this.listAnswers = listAnswers;
    }

    @Override
    public int getCount() {
        return listAnswers.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Button btnAnswer;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);

        btnAnswer = (Button) view.findViewById(R.id.buttonAnswer);
        String item = listAnswers.get(i);
        btnAnswer.setText(item);

        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.equals(rightAnswer)) {
                    // tat bao thuc
                    Intent myIntent = new Intent(context, AlarmService.class);
                    context.startService(myIntent);
                    myIntent.putExtra("extra", "off");
                    Toast.makeText(context, "Tắt báo thức thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Tỉnh lại đi, không ai cứu được con đâu!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

}
