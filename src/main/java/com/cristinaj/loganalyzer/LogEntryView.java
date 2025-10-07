package com.cristinaj.loganalyzer;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LogEntryView {
    private Long id;
    private String timestamp;
    private String level;
    private String message;
    private String source;
    private String meta;
}
