package com.nl2sql.service;

import org.springframework.stereotype.Service;

@Service
public class SqlSafetyValidator {

    private static final String[] BLOCKED_KEYWORDS = {
            "insert", "update", "delete", "drop", "alter",
            "truncate", "create", "grant", "revoke", "--", ";"
    };

    public void assertSafeSelect(String sql) {
        String normalized = sql.trim().toLowerCase();

        if (!normalized.startsWith("select")) {
            throw new IllegalArgumentException("Only SELECT queries are allowed. Claude returned: " + sql);
        }

        for (String keyword : BLOCKED_KEYWORDS) {
            if (normalized.contains(keyword)) {
                throw new IllegalArgumentException(
                        "Query contains a disallowed keyword ('" + keyword + "'): " + sql);
            }
        }
    }
}