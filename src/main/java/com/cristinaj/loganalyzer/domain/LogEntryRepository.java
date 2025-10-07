package com.cristinaj.loganalyzer.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    Page<LogEntry> findByLevel(LogLevel level, Pageable pageable);
}
