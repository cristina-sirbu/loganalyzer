package com.cristinaj.loganalyzer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "log_entry")
public class LogEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column(name = "ts_utc", nullable = false)
    private Instant tsUtc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogLevel level;

    @Lob
    @Column(nullable = false)
    private String message;

    @Column(length = 128)
    private String source;

    @Lob
    private String metaJson;
}
