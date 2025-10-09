# Log Analyzer

Log Analyzer is a simple microservice for log ingestion and analysis.

---
## Features

* Ingest logs `POST /logs`
* Upload and ingest files with logs `POST /logs/upload`
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

---
## Running instructions

### Running locally (Dev profile)

Run the application locally:
```shell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Test the application:
```shell
curl -X POST http://localhost:8080/logs \
  -H "Content-Type: application/json" \
  -d '[{"timestamp":"2025-10-06T10:23:45Z","level":"ERROR","message":"from-dev"}]'
```
It should return ``{"saved":1,"failed":0}``.

### Docker (locally)

Build the image:
```shell
docker build -t log-analyzer:local .
```

Run the image:
```shell
mkdir -p data
docker run --rm -p 8080:8080 -v "$(pwd)/data:/data" -e SPRING_PROFILES_ACTIVE=docker --name log-analyzer log-analyzer:local
```

Test the application:
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

### Helm

**Prerequisites**:
* A Kubernetes cluster. (I used Minikube for testing)
* Helm installed.

Create a namespace.
```shell
kubectl create ns log-analyzer
```

Deploy helm chart.
```shell
helm install log-analyzer ./helm/log-analyzer -n log-analyzer
```

Check deployment.
```shell
kubectl get deploy,po,svc -n log-analyzer
```

Check application by port-forwarding.
```shell
kubectl port-forward svc/log-analyzer 8080:8080 -n log-analyzer
curl -s http://localhost:8080/logs
```

---
## Potential improvements

* Moving from H2 to Postgresql or another DB.
* Splitting into two services for clear separation of concerns and possibility to scale independently:
  * Log Ingestion Service: for write path.
  * Log Analysis Service: for read path.
* Improving the filtering API by:
  * allowing to search in all messages based on a word.
  * allowing to filter based on source.
* If traffic grows:
  * indexes on (timestamp) or (level, timestamp) could help.
  * implementing HPA (Horizontal Pod Autoscaling) in the Helm chart could help.
* Improving the coverage of unit tests and integration tests.
* If helm chart deployed on a non-local Kubernetes cluster, an Ingress may be required to be added.
* Adding to the GitHub Workflow a step to push the Helm chart on release.
