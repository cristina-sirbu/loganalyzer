package com.cristinaj.loganalyzer;

import com.cristinaj.loganalyzer.domain.LogEntry;
import com.cristinaj.loganalyzer.domain.LogEntryRepository;
import com.cristinaj.loganalyzer.ingestion.LogIngestionService;
import com.cristinaj.loganalyzer.ingestion.dto.IngestionResult;
import com.cristinaj.loganalyzer.ingestion.dto.LogEntryCreate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogIngestionServiceTest {

    @Mock
    LogEntryRepository repo;

    @Test
    void ingest_savesOneValidItem() {
        LogIngestionService service = new LogIngestionService(repo);

        LogEntryCreate logEntryCreate = new LogEntryCreate();
        logEntryCreate.setTimestamp("2025-10-06T10:00:00Z");
        logEntryCreate.setLevel("ERROR");
        logEntryCreate.setMessage("ok");
        logEntryCreate.setSource("test");
        logEntryCreate.setMeta(null);

        List<LogEntryCreate> batch = new ArrayList<>();
        batch.add(logEntryCreate);

        when(repo.saveAll(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        IngestionResult result = service.ingest(batch);

        verify(repo).saveAll(argThat(list -> {
            if (! (list instanceof List<LogEntry>)) return false;
            return ((List<LogEntry>) list).size() == 1;
        }));

        assertEquals(1,result.getSaved());
        assertEquals(0, result.getFailed());
    }

    @Test
    void ingest_invalidInput() {
        LogIngestionService service = new LogIngestionService(repo);

        // One valid logEntry
        LogEntryCreate validLog = new LogEntryCreate();
        validLog.setTimestamp("2025-10-06T10:00:00Z");
        validLog.setLevel("ERROR");
        validLog.setMessage("ok");
        validLog.setSource("test");
        validLog.setMeta(null);

        // One invalid logEntry (Bad Log Level)
        LogEntryCreate invalidLogLevel = new LogEntryCreate();
        invalidLogLevel.setTimestamp("2025-10-06T10:00:00Z");
        invalidLogLevel.setLevel("INVALID");
        invalidLogLevel.setMessage("ok");
        invalidLogLevel.setSource("test");
        invalidLogLevel.setMeta(null);

        // One invalid logEntry (invalid format for timestamp)
        LogEntryCreate invalidLogTimestamp = new LogEntryCreate();
        invalidLogTimestamp.setTimestamp("Invalid timestamp");
        invalidLogTimestamp.setLevel("ERROR");
        invalidLogTimestamp.setMessage("ok");
        invalidLogTimestamp.setSource("test");
        invalidLogTimestamp.setMeta(null);

        List<LogEntryCreate> batch = new ArrayList<>();
        batch.add(validLog);
        batch.add(invalidLogLevel);
        batch.add(invalidLogTimestamp);

        when(repo.saveAll(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        IngestionResult result = service.ingest(batch);

        verify(repo).saveAll(argThat(list -> {
            if (! (list instanceof List<LogEntry>)) return false;
            return ((List<LogEntry>) list).size() == 1;
        }));

        assertEquals(1,result.getSaved());
        assertEquals(2, result.getFailed());
    }
}
