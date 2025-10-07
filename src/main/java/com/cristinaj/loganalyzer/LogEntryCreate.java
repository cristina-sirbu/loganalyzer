package com.cristinaj.loganalyzer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LogEntryCreate {

    @NotNull(message = "timestamp is required")
    private String timestamp;

    @NotNull(message = "level is required")
    private String level;

    @NotNull(message = "message is required")
    @NotBlank(message = "message must not be blank")
    private String message;

    @Size(max = 128, message = "source must be at most 128 characters")
    private String source;

    private String meta;
}
