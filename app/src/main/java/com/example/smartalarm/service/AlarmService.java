package com.example.smartalarm.service;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.smartalarm.R;
import com.example.smartalarm.adapter.QuestionAdapter;
import com.example.smartalarm.model.ListQuestion;
import com.example.smartalarm.model.Question;

import java.util.Arrays;
import java.util.Random;

public class AlarmService extends Service {

    MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String string = intent.getExtras().getString("extra");
        boolean isStatus = string.equals("on") ? true : false;
        if (isStatus) {
            Toast.makeText(this, "Test Music success!", Toast.LENGTH_SHORT).show();
//        return super.onStartCommand(intent, flags, startId);
            mediaPlayer = MediaPlayer.create(this, R.raw.jingle_bells);
            mediaPlayer.start();
        } else {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        // alert dialog -> question
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_question);
        dialog.show();
        Log.e("DIALOG", "SHOW DIALOG SUCCESS");

        // random questions
        ListQuestion listQuestion = new ListQuestion();
        Random random = new Random();
        int indexQuestion = random.nextInt(listQuestion.listQuestions.size());
        Log.e("DIALOG", "RANDOM " + indexQuestion);


        // show question
        TextView tvQuestion = (TextView) dialog.findViewById(R.id.textViewQuestion);
        ListView lvAnswer = (ListView) dialog.findViewById(R.id.listViewAnswer);
        Question questionSelected = listQuestion.getListQuestions().get(indexQuestion);
        QuestionAdapter questionAdapter = new QuestionAdapter(this, R.layout.item_answer_question, Arrays.asList(questionSelected.getAnswer()), questionSelected.getRightAnswer());
        Log.e("DIALOG", "SHOW QUESTION SUCCESS");


        return START_NOT_STICKY;
    }
}
