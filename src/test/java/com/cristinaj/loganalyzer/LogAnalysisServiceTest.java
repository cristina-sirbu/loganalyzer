package com.cristinaj.loganalyzer;

import com.cristinaj.loganalyzer.analysis.LogAnalysisService;
import com.cristinaj.loganalyzer.analysis.LogEntryView;
import com.cristinaj.loganalyzer.domain.LogEntry;
import com.cristinaj.loganalyzer.domain.LogEntryRepository;
import com.cristinaj.loganalyzer.domain.LogLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogAnalysisServiceTest {

    @Mock
    LogEntryRepository repo;

    @Test
    void getLogs_noFilters_callsFindAll_andMaps() {
        LogAnalysisService logAnalysisService = new LogAnalysisService(repo);
        Instant now = Instant.parse("2025-10-06T10:00:00Z");

        LogEntry entity = new LogEntry();
        entity.setId(1234L);
        entity.setTsUtc(now);
        entity.setLevel(LogLevel.ERROR);
        entity.setMessage("message");
        entity.setSource("source");
        entity.setMetaJson("{\"abc\":1}");

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "tsUtc"));
        when(repo.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

        Page<LogEntryView> page = logAnalysisService.getLogs(null, null, null, pageable);

        verify(repo).findAll(pageable);
        verifyNoMoreInteractions(repo);

        assertEquals(1, page.getTotalElements());
        LogEntryView logEntryView = page.getContent().getFirst();
        assertEquals(1234L, logEntryView.getId());
        assertEquals("2025-10-06T10:00:00Z", logEntryView.getTimestamp());
        assertEquals("ERROR", logEntryView.getLevel());
        assertEquals("message", logEntryView.getMessage());
        assertEquals("source", logEntryView.getSource());
        assertEquals("{\"abc\":1}", logEntryView.getMeta());
    }

    @Test
    void getLogs_levelOnly_callsFindByLevel() {
        LogAnalysisService logAnalysisService = new LogAnalysisService(repo);
        Pageable pageable = PageRequest.of(0, 10);
        when(repo.findByLevel(LogLevel.WARN, pageable)).thenReturn(Page.empty(pageable));

        Page<LogEntryView> page = logAnalysisService.getLogs(LogLevel.WARN, null, null, pageable);

        verify(repo).findByLevel(LogLevel.WARN, pageable);
        assertEquals(0, page.getTotalElements());
    }

    @Test
    void getLogs_rangeOnly_callsFindByTsBetween() {
        LogAnalysisService logAnalysisService = new LogAnalysisService(repo);
        Pageable pageable = PageRequest.of(0, 10);
        var from = Instant.parse("2025-10-06T00:00:00Z");
        var to = Instant.parse("2025-10-06T23:59:59Z");

        when(repo.findByTsUtcBetween(from, to, pageable)).thenReturn(Page.empty(pageable));

        var page = logAnalysisService.getLogs(null, from, to, pageable);

        verify(repo).findByTsUtcBetween(from, to, pageable);
        assertTrue(page.getContent().isEmpty());
    }

    @Test
    void getLogs_levelAndRange_callsFindByLevelAndTsBetween() {
        LogAnalysisService logAnalysisService = new LogAnalysisService(repo);
        Pageable pageable = PageRequest.of(0, 10);
        var from = Instant.parse("2025-10-06T00:00:00Z");
        var to = Instant.parse("2025-10-06T23:59:59Z");

        when(repo.findByLevelAndTsUtcBetween(LogLevel.INFO, from, to, pageable))
                .thenReturn(Page.empty(pageable));

        Page<LogEntryView> page = logAnalysisService.getLogs(LogLevel.INFO, from, to, pageable);

        verify(repo).findByLevelAndTsUtcBetween(LogLevel.INFO, from, to, pageable);
        assertEquals(0, page.getTotalElements());
    }
}
