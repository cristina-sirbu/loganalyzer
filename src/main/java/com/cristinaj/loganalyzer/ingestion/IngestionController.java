package com.cristinaj.loganalyzer.ingestion;

import com.cristinaj.loganalyzer.ingestion.dto.IngestionResult;
import com.cristinaj.loganalyzer.ingestion.dto.LogEntryCreate;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/logs")
public class IngestionController {

    private final LogIngestionService logIngestionService;

    public IngestionController(LogIngestionService logIngestionService) {
        this.logIngestionService = logIngestionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IngestionResult ingest(@RequestBody List<@Valid LogEntryCreate> batch) {
        return logIngestionService.ingest(batch);
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public IngestionResult upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String source
    ) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is empty");
        }

        LogLineParser parser = new LogLineParser();

        int parseFailures = 0;
        List<LogEntryCreate> batch = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                Optional<LogEntryCreate> opt = parser.parse(line, source);
                if (opt.isPresent()) {
                    batch.add(opt.get());
                } else {
                    parseFailures++;
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("failed to read file: " + e.getMessage());
        }

        IngestionResult svcResult = logIngestionService.ingest(batch);

        IngestionResult result = new IngestionResult();
        result.setSaved(svcResult.getSaved());
        result.setFailed(svcResult.getFailed()+parseFailures);

        return result;
    }
}
