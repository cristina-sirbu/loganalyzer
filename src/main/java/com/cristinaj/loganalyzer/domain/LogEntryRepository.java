package com.cristinaj.loganalyzer.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    Page<LogEntry> findByLevel(LogLevel level, Pageable pageable);
    Page<LogEntry> findByTsUtcBetween(Instant from, Instant to, Pageable pageable);
    Page<LogEntry> findByLevelAndTsUtcBetween(LogLevel level, Instant from, Instant to, Pageable pageable);
}
