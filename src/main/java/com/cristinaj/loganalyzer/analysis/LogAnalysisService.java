package com.cristinaj.loganalyzer.analysis;

import com.cristinaj.loganalyzer.domain.LogEntry;
import com.cristinaj.loganalyzer.domain.LogEntryRepository;
import com.cristinaj.loganalyzer.domain.LogLevel;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class LogAnalysisService {

    private final LogEntryRepository logEntryRepository;

    public LogAnalysisService(LogEntryRepository logEntryRepository) {
        this.logEntryRepository = logEntryRepository;
    }

    public Page<LogEntryView> getLogs(
            @Nullable LogLevel logLevel,
            @Nullable Instant from,
            @Nullable Instant to,
            Pageable pageable
    ) {
        Page<LogEntry> result;
        if (logLevel == null && from == null && to == null)
            result = logEntryRepository.findAll(pageable);
        else if (logLevel != null && from == null && to == null)
            result = logEntryRepository.findByLevel(logLevel, pageable);
        else if (logLevel == null && from != null && to != null)
            result = logEntryRepository.findByTsUtcBetween(from, to, pageable);
        else
            result = logEntryRepository.findByLevelAndTsUtcBetween(logLevel, from, to, pageable);

        return result.map(e -> LogEntryView.builder()
                .id(e.getId())
                .timestamp(e.getTsUtc().toString())
                .level(e.getLevel().name())
                .message(e.getMessage())
                .source(e.getSource())
                .meta(e.getMetaJson())
                .build());
    }
}
