# Log Analyzer

Work in progress...

## Database schema

| Column      | Type            | Constraints                 | Description                                        |
|-------------|-----------------|-----------------------------|----------------------------------------------------|
| `id`        | BIGINT          | PRIMARY KEY, AUTO_INCREMENT | Unique identifier for each log entry.              |
| `ts_utc`    | TIMESTAMP (UTC) | NOT NULL                    | Timestamp of when the log event occurred (in UTC). |
| `level`     | VARCHAR(20)     | NOT NULL                    | Log severity level (e.g., INFO, WARN, ERROR).      |
| `message`   | TEXT            | NOT NULL                    | The log message body.                              |
| `source`    | VARCHAR(128)    | NULLABLE                    | Origin of the log (service, host, or file).        |
| `meta_json` | TEXT (JSON)     | NULLABLE                    | Optional metadata in JSON format.                  |

Notes:
• Timestamps are stored in UTC (Instant).
• meta_json holds optional fields, e.g. {"orderId":123,"durationMs":520}.