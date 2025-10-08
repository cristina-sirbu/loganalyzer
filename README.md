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

## Deployment instructions

### Docker (locally)

To build the image run:
```shell
docker build -t log-analyzer:local .
```

To run the image:
```shell
mkdir -p data
docker run --rm -p 8080:8080 -v "$(pwd)/data:/data" -e SPRING_PROFILES_ACTIVE=docker --name log-analyzer log-analyzer:local
```

To check if the application is running:
```shell
curl -s -X POST "http://localhost:8080/logs" \
  -H "Content-Type: application/json" \
  -d '[{"timestamp":"2025-10-06T10:23:45Z","level":"ERROR","message":"x"}]'
```
It should return ``{"saved":1,"failed":0}``.

Another way to check the app is to run:
```shell
curl -s "http://localhost:8080/logs"
```
This should return a JSON with the log entries.

To verify if persistence work, stop the container, run it again and check if `GET /logs` returns the same entries:
```shell
docker stop log-analyzer
docker run --rm -p 8080:8080 -v "$(pwd)/data:/data" -e SPRING_PROFILES_ACTIVE=docker --name log-analyzer log-analyzer:local
curl -s "http://localhost:8080/logs"
```