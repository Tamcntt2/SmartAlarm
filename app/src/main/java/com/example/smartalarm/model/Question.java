package com.example.smartalarm.model;

public class Question {
    public String query;
    public String[] answer;
    public String rightAnswer;

    public Question(String query, String[] answer, String rightAnswer) {
        this.query = query;
        this.answer = answer;
        this.rightAnswer = rightAnswer;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public String getQuery() {
        return query;
    }

    public String[] getAnswer() {
        return answer;
    }
}
