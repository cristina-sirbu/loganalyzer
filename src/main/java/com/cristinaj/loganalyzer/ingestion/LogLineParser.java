package com.cristinaj.loganalyzer.ingestion;

import com.cristinaj.loganalyzer.ingestion.dto.LogEntryCreate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogLineParser {

    // Example plain-text line:
    // 2025-10-06T10:23:45Z ERROR Payment failed [source=billing-service]
    private static final Pattern PLAIN_PATTERN = Pattern.compile(
            "^\\s*(\\S+)\\s+(\\w+)\\s+(.*?)(?:\\s+\\[source=(.+?)\\])?\\s*$"
    );


    // Try to parse a single line into a LogEntryCreate.
    // Strategy:
    // 1) If line starts with '{' -> try JSON
    // 2) Else -> try plain text regex (timestamp, level, message, optional [source=...])
    public Optional<LogEntryCreate> parse(String line, String defaultSource) {
        if (line == null) return Optional.empty();
        String trimmed = line.trim();
        if (trimmed.isEmpty()) return Optional.empty();

        if (trimmed.startsWith("{")) {
            return tryJson(trimmed);
        }
        return tryPlain(trimmed, defaultSource);
    }

    private Optional<LogEntryCreate> tryJson(String json) {
        try {
            return Optional.of(new ObjectMapper().readValue(json, LogEntryCreate.class));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private Optional<LogEntryCreate> tryPlain(String line, String defaultSource) {
        Matcher m = PLAIN_PATTERN.matcher(line);
        if (!m.matches()) return Optional.empty();

        String ts = m.group(1);
        String level = m.group(2);
        String message = m.group(3);
        String source = m.group(4) != null ? m.group(4) : defaultSource;

        LogEntryCreate dto = new LogEntryCreate();
        dto.setTimestamp(ts);
        dto.setLevel(level);
        dto.setMessage(message != null ? message.trim() : null);
        dto.setSource(source);

        return Optional.of(dto);
    }
}
