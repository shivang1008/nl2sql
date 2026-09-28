package com.nl2sql.service;

import com.nl2sql.dto.QueryResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QueryService {

    private static final String SYSTEM_PROMPT = """
            You are a MySQL expert that converts natural language questions into SQL.
            Rules:
            - Only ever write a single SELECT statement.
            - Never use INSERT, UPDATE, DELETE, DROP, ALTER, or any statement that changes data.
            - Use only the tables and columns given in the schema below.
            - Return ONLY the raw SQL query. No explanation, no markdown formatting, no backticks.
            """;

    private final SchemaService schemaService;
    private final GeminiClient claudeClient;
    private final SqlSafetyValidator safetyValidator;
    private final JdbcTemplate jdbcTemplate;

    public QueryService(SchemaService schemaService,
                         GeminiClient claudeClient,
                         SqlSafetyValidator safetyValidator,
                         JdbcTemplate jdbcTemplate) {
        this.schemaService = schemaService;
        this.claudeClient = claudeClient;
        this.safetyValidator = safetyValidator;
        this.jdbcTemplate = jdbcTemplate;
    }

    public QueryResponse handleQuestion(String question) {
        String schema = schemaService.describeSchema();

        String userPrompt = "Schema:\n" + schema + "\nQuestion: " + question;
        String generatedSql = claudeClient.ask(SYSTEM_PROMPT, userPrompt);

        safetyValidator.assertSafeSelect(generatedSql);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(generatedSql);

        return new QueryResponse(question, generatedSql, rows);
    }
}