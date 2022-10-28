package com.example.smartalarm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartalarm.R;
import com.example.smartalarm.service.AlarmService;

import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswersViewHolder> {

    private Context context;
    private List<String> listAnswers;
    private String rightAnswer;

    public AnswersAdapter(Context context, List<String> listAnswers, String rightAnswer) {
        this.context = context;
        this.listAnswers = listAnswers;
        this.rightAnswer = rightAnswer;
    }

    @NonNull
    @Override
    public AnswersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer_question, parent, false);

        return new AnswersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswersViewHolder holder, int position) {
        String answer = listAnswers.get(position);
        if (answer == null) return;

        holder.btnAnswer.setText(answer);

        holder.btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answer.equals(rightAnswer)) {
                    // turn off
                    Intent myIntent = new Intent(context, AlarmService.class);
                    context.startService(myIntent);
                    myIntent.putExtra("extra", false);
                    Toast.makeText(context, "Tắt báo thức thành công!", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                } else {
                    Toast.makeText(context, "Đáp án sai!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAnswers.size();
    }

    public class AnswersViewHolder extends RecyclerView.ViewHolder {
        Button btnAnswer;

        public AnswersViewHolder(@NonNull View itemView) {
            super(itemView);
            btnAnswer = (Button) itemView.findViewById(R.id.buttonAnswer);
        }
    }
}
