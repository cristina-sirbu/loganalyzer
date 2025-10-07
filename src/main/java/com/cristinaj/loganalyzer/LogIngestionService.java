package com.cristinaj.loganalyzer;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class LogIngestionService {

    private final LogEntryRepository logEntryRepository;

    public LogIngestionService(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    public IngestionResult ingest(List<LogEntryCreate> batch) {
        if (batch == null || batch.isEmpty()) throw new IllegalArgumentException("No log entry to ingest.");

        List<LogEntryEntity> validEntities = new ArrayList<>();
        IngestionResult result = new IngestionResult();

        for (LogEntryCreate log:batch) {
            try {
                // timestamp: accept Z or ±hh:mm
                Instant ts = OffsetDateTime.parse(log.getTimestamp()).toInstant();

                // enum mapping: case-insensitive
                LogLevel level = LogLevel.valueOf(log.getLevel().toUpperCase(Locale.ROOT));

                LogEntryEntity logEntry = new LogEntryEntity();
                logEntry.setTsUtc(ts);
                logEntry.setLevel(level);
                logEntry.setMessage(log.getMessage());
                logEntry.setSource(log.getSource());
                logEntry.setMetaJson(log.getMeta());
                validEntities.add(logEntry);
            } catch (Exception e) {
                result.setFailed(result.getFailed()+1);
            }
        }

        logEntryRepository.saveAll(validEntities);
        result.setSaved(validEntities.size());

        return result;
    }
}
