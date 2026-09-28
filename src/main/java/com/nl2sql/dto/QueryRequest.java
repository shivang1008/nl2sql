package com.nl2sql.dto;

public class QueryRequest {

    private String question;

    public QueryRequest() {
        // needed by Jackson to build this object from incoming JSON
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}