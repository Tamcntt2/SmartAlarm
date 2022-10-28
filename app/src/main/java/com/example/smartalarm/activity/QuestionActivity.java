package com.example.smartalarm.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.smartalarm.R;
import com.example.smartalarm.adapter.AnswersAdapter;
import com.example.smartalarm.util.QuestionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        // random questions
        QuestionUtils listQuestion = new QuestionUtils();
        Random random = new Random();
        int indexQuestion = random.nextInt(listQuestion.getListQuestions().size());
        QuestionUtils questionRandom = listQuestion.getListQuestions().get(indexQuestion);
        List listAnswer = new ArrayList<>(Arrays.asList(questionRandom.getAnswer()));

        // set question
        TextView tvQuestion = (TextView) findViewById(R.id.textViewQuestion);
        tvQuestion.setText(questionRandom.getQuery());

        // recycler view answer
        RecyclerView rcvAnswer = (RecyclerView) findViewById(R.id.recyclerViewAnswer) ;
        AnswersAdapter answersAdapter = new AnswersAdapter(this, listAnswer, questionRandom.getRightAnswer());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcvAnswer.setAdapter(answersAdapter);
        rcvAnswer.setLayoutManager(linearLayoutManager);

    }
}