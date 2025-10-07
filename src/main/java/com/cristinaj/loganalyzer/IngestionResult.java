package com.cristinaj.loganalyzer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngestionResult {
    private int saved;
    private int failed;
}
