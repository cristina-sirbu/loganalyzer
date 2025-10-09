package com.cristinaj.loganalyzer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LogAnalyzerApiIT {

    @LocalServerPort
    int port;

    private WebTestClient client(String baseUrl) {
        return WebTestClient.bindToServer()
                .baseUrl(baseUrl)
                .build();
    }

    @Test
    void roundTrip_ingest_then_query() {
        var base = "http://localhost:" + port;
        var web = client(base);

        String payload = """
          [
            {"timestamp":"2025-10-06T10:23:45Z","level":"ERROR","message":"e1","source":"it"},
            {"timestamp":"2025-10-06T10:24:00Z","level":"INFO","message":"e2","source":"it"}
          ]
          """;

        web.post()
                .uri("/logs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.saved").isEqualTo(2)
                .jsonPath("$.failed").isEqualTo(0);

        web.get()
                .uri("/logs")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(2)
                .jsonPath("$.content[0].message").exists();
    }

    @Test
    void ingest_emptyBody_returns400_withBadJson() {
        var base = "http://localhost:" + port;
        var web = WebTestClient.bindToServer().baseUrl(base).build();

        web.post()
                .uri("/logs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("") // empty body
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("BAD_JSON")
                .jsonPath("$.message").exists();
    }
}
