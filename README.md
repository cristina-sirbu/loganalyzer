# Log Analyzer

Work in progress...

Log Analyzer is a simple microservice for log ingestion and analysis.

---
## Features

* Ingest logs `POST /logs`
* Query logs `GET /logs`
* Filtering by level and time range `GET /logs?level=ERROR&from=...&to=...`
* Persists logs in a H2 file DB

Notes:
* Time filter applies only when both `from` and `to` are provided.
* Pagination defaults: size=50, sorted by `tsUtc` descending, max 500 logs per page.

---
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

* Timestamps are stored in UTC (Instant).
* `meta_json` holds optional fields, e.g. {"orderId":123,"durationMs":520}.

## Running instructions

### Running locally (Dev profile)

To run the application:
```shell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

To test the application:
```shell
curl -X POST http://localhost:8080/logs \
  -H "Content-Type: application/json" \
  -d '[{"timestamp":"2025-10-06T10:23:45Z","level":"ERROR","message":"from-dev"}]'
```
It should return ``{"saved":1,"failed":0}``.

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

To test the application:
```shell
curl -X POST http://localhost:8080/logs \
  -H "Content-Type: application/json" \
  -d '[{"timestamp":"2025-10-06T10:24:45Z","level":"ERROR","message":"from-docker"}]'
```
It should return ``{"saved":1,"failed":0}``.

Another way to check the app is to run:
```shell
curl -s "http://localhost:8080/logs"
```
This should return a JSON with the log entries.

<details>
<summary>How to verify persistence</summary>

To verify if persistence work, stop the container, run it again and check if `GET /logs` returns the same entries:
```shell
docker stop log-analyzer
docker run --rm -p 8080:8080 -v "$(pwd)/data:/data" -e SPRING_PROFILES_ACTIVE=docker --name log-analyzer log-analyzer:local
curl -s "http://localhost:8080/logs"
```
</details>

### Docker (GHCR)

The Github Workflow builds and pushes images after successful build on `main` or any other branch.

| Branch / Commit   | Image Tag      | Example                                        |
|-------------------|----------------|------------------------------------------------|
| main branch       | latest         | ghcr.io/cristina-sirbu/loganalyzer:latest      |
| Any branch or PR	 | sha-<shortsha> | ghcr.io/cristina-sirbu/loganalyzer:sha-dde5127 |

Both `linux/amd64` and `linux/arm64` architectures are included in each image manifest.

```shell
docker pull ghcr.io/cristina-sirbu/loganalyzer:latest
docker run --rm -p 8080:8080 -v "$(pwd)/data:/data" \
  -e SPRING_PROFILES_ACTIVE=docker \
  ghcr.io/cristina-sirbu/loganalyzer:latest
```