package com.example.smartalarm.model;

import java.util.ArrayList;
import java.util.List;

public class ListQuestion {
    public List<Question> listQuestions;

    public List<Question> getListQuestions() {
        return listQuestions;
    }

    public ListQuestion() {
        listQuestions = new ArrayList<>();
        listQuestions.add(new Question("1 + 1 = ?", new String[]{"0", "1", "2", "3"}, "2"));
        listQuestions.add(new Question("6 + 2 = ?", new String[]{"7", "-8", "8"}, "8"));
        listQuestions.add(new Question("2 + 5 = ?", new String[]{"9", "5", "2", "7"}, "7"));
        listQuestions.add(new Question("8 + 7 = ?", new String[]{"15", "8", "13", "5"}, "15"));
    }


}
