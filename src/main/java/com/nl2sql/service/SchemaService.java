package com.nl2sql.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class SchemaService {

    private final JdbcTemplate jdbcTemplate;

    public SchemaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // PERCEIVE step: reads actual tables/columns from MySQL instead of
    // hardcoding them, so the agent always knows the current DB shape.
    public String describeSchema() {
        StringBuilder schema = new StringBuilder();

        try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();

            try (ResultSet tables = meta.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    schema.append("Table: ").append(tableName).append("\n");

                    try (ResultSet columns = meta.getColumns(conn.getCatalog(), null, tableName, "%")) {
                        while (columns.next()) {
                            String colName = columns.getString("COLUMN_NAME");
                            String colType = columns.getString("TYPE_NAME");
                            schema.append("  - ").append(colName).append(" (").append(colType).append(")\n");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read database schema", e);
        }

        return schema.toString();
    }
}