package com.cristinaj.loganalyzer;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class IngestionController {

    private final LogIngestionService logIngestionService;

    public IngestionController(LogIngestionService logIngestionService) {
        this.logIngestionService = logIngestionService;
    }

    @PostMapping
    public IngestionResult ingest(@RequestBody List<@Valid LogEntryCreate> batch) {
        return logIngestionService.ingest(batch);
    }
}
