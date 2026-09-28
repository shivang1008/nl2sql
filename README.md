# nl2sql

Ask a MySQL database questions in plain English. A Spring Boot backend reads the live database schema, asks Google's Gemini API to write a SQL query, checks that the query is read-only, runs it, and returns both the SQL and the results. A small browser page is included so you can try it without `curl`.

## How it works

Each request goes through four steps:

1. **Read the schema.** `SchemaService` uses JDBC metadata to list the tables and columns that exist right now, so nothing is hardcoded in the prompt.
2. **Generate SQL.** `GeminiClient` sends the schema and the question to the Gemini API and gets a SQL query back.
3. **Validate.** `SqlSafetyValidator` rejects anything that is not a single `SELECT` statement.
4. **Execute.** `QueryService` runs the validated query with `JdbcTemplate` and returns the SQL and the rows.

```
Browser / curl
     |  POST /api/query  {"question": "..."}
     v
QueryController
     v
QueryService
     |-- SchemaService        reads tables and columns from MySQL
     |-- GeminiClient         asks Gemini for SQL
     |-- SqlSafetyValidator   rejects anything that is not a SELECT
     '-- JdbcTemplate         runs the query
     v
{ question, generatedSql, results }
```

This is a single pass: there is no retry loop that feeds SQL errors back to the model.

## Tech stack

- Java 17+, Spring Boot 3.3
- MySQL 8, Spring JDBC
- Google Gemini API (free tier)
- Plain HTML, CSS and JavaScript for the UI

## Setup

**Prerequisites:** Java 17 or newer, MySQL running locally, and a free Gemini API key from [Google AI Studio](https://aistudio.google.com/apikey).

1. Create the database:
```sql
   CREATE DATABASE nl2sql_demo;
```
   The `customers` and `orders` tables and some sample rows are created automatically on startup from `schema.sql` and `data.sql`. The sample data is reset every time the app starts.

2. Set two environment variables. Secrets are read from the environment and are never stored in the repo:
```bash
   export DB_PASSWORD=your_mysql_root_password
   export GEMINI_API_KEY=your_gemini_api_key
```
   In Eclipse, add them under **Run Configurations → Environment** instead.

3. Run the app: start `NlToSqlApplication` from your IDE, or use `mvn spring-boot:run` if Maven is installed.

4. Open http://localhost:8080 and ask a question.

The model name is set in `src/main/resources/application.properties` (`gemini.api.model`). Google retires model names over time, so if the API returns a 404 about the model, update that value.

## Example

```bash
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d '{"question": "Which customer has spent the most money in total?"}'
```

Response:

```json
{
  "question": "Which customer has spent the most money in total?",
  "generatedSql": "SELECT customers.name FROM customers JOIN orders ON customers.id = orders.customer_id GROUP BY customers.id, customers.name ORDER BY SUM(orders.amount) DESC LIMIT 1",
  "results": [{ "name": "Rahul Sharma" }]
}
```

Other questions to try: "How many orders has each customer placed?", "What is the total revenue across all orders?", "List all orders placed in September 2026."

## Project structure

```
src/main/java/com/nl2sql/
  NlToSqlApplication.java
  config/AppConfig.java              RestTemplate bean
  controller/QueryController.java    POST /api/query
  dto/                               QueryRequest, QueryResponse
  service/
    SchemaService.java
    GeminiClient.java
    SqlSafetyValidator.java
    QueryService.java
src/main/resources/
  application.properties
  schema.sql, data.sql               sample database
  static/index.html                  browser UI
```

## Limitations

- The safety check is keyword matching, not real SQL parsing. A production system should parse the SQL properly and also run queries as a database user that only has `SELECT` privileges.
- There is no authentication, so do not expose the endpoint publicly.
- The schema is read on every request and the generated SQL is not checked against the intent of the question.
- Gemini can return temporary `503` overload errors, and the app does not retry them yet.

## Possible next steps

- Retry on transient API errors
- Feed SQL errors back to the model for a second attempt
- Cache the schema
- Add authentication and a read-only database user