package com.nl2sql.dto;

import java.util.List;
import java.util.Map;

public class QueryResponse {

    private final String question;
    private final String generatedSql;
    private final List<Map<String, Object>> results;

    public QueryResponse(String question, String generatedSql, List<Map<String, Object>> results) {
        this.question = question;
        this.generatedSql = generatedSql;
        this.results = results;
    }

    public String getQuestion() { return question; }
    public String getGeneratedSql() { return generatedSql; }
    public List<Map<String, Object>> getResults() { return results; }
}