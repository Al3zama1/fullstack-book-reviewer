package com.example.fullstackbookreviewer.event;

import com.example.fullstackbookreviewer.AbstractIntegrationTest;
import com.example.fullstackbookreviewer.mapstruct.dto.BookSynchronization;
import com.example.fullstackbookreviewer.repository.BookRepository;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.time.Duration;

import static org.awaitility.Awaitility.given;

public class BookSynchronizationListenerIT extends AbstractIntegrationTest {
    private static final String ISBN = "9780596004651";
    @Value("${messaging.kafka.topic}")
    private String synchronizationTopic;
    private static String VALID_RESPONSE;

    @Autowired
    KafkaTemplate<String, BookSynchronization> kafkaTemplate;
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookRepository bookRepository;

    static {
        try {
            VALID_RESPONSE = new String(BookSynchronizationListenerIT.class
                    .getClassLoader()
                    .getResourceAsStream("stubs/openlibrary/success-" + ISBN + ".json")
                    .readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGetSuccessWhenClientIsAuthenticated() throws JOSEException {
        webTestClient.get().uri("/api/v1/books/reviews/statistics")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getSignedJWT())
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void shouldReturnBookFromAPIWhenApplicationConsumesNewSyncRequest() {
        webTestClient.get().uri("/api/v1/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.size()").isEqualTo(0);

        openLibraryStubs.stubForSuccessfulBookResponse(ISBN, VALID_RESPONSE);

        kafkaTemplate.send(synchronizationTopic, new BookSynchronization(ISBN));

        given().atMost(Duration.ofSeconds(5))
                .await()
                .untilAsserted(() -> {
                    webTestClient.get().uri("/api/v1/books")
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody()
                            .jsonPath("$.size()").isEqualTo(1)
                            .jsonPath("$[0].isbn").isEqualTo(ISBN);
                });

    }
}
