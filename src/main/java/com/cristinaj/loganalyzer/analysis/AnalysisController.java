package com.cristinaj.loganalyzer.analysis;

import com.cristinaj.loganalyzer.domain.LogLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class AnalysisController {

    private final LogAnalysisService logAnalysisService;

    public AnalysisController(LogAnalysisService logAnalysisService) {
        this.logAnalysisService = logAnalysisService;
    }

    @GetMapping("/logs")
    public Page<LogEntryView> getLogs(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @PageableDefault(size = 50, sort = "tsUtc", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (from != null && to != null && from.isAfter(to)) throw new IllegalArgumentException("from must be <= to");

        LogLevel parsedLevel = null;
        if (level != null && !level.isBlank()) {
            parsedLevel = LogLevel.valueOf(level.toUpperCase());
        }

        if (pageable.getPageSize() > 500) {
            pageable = PageRequest.of(pageable.getPageNumber(), 500, pageable.getSort());
        }

        return logAnalysisService.getLogs(parsedLevel, from, to, pageable);
    }
}
