package com.cristinaj.loganalyzer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEntryRepository extends JpaRepository<LogEntryEntity, Long> {
    Page<LogEntryEntity> findByLevel(LogLevel level, Pageable pageable);
}
