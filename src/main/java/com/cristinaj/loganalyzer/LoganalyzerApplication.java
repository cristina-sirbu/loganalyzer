package com.cristinaj.loganalyzer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Log Analyzer API",
                version = "1.0",
                description = "Endpoints for log ingestion and analysis"
        )
)
@SpringBootApplication
public class LoganalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoganalyzerApplication.class, args);
	}

}
