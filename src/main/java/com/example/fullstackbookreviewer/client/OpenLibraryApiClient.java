package com.example.fullstackbookreviewer.client;

import com.example.fullstackbookreviewer.entity.Book;
import com.example.fullstackbookreviewer.mapstruct.mapper.JsonNodeToBookMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OpenLibraryApiClient {
    private final WebClient openLibraryWebClient;
    private final JsonNodeToBookMapper jsonNodeToBookMapper;

    public Book fetchMetadataForBook(String isbn) {
        ObjectNode result = openLibraryWebClient.get().uri("/api/books",
                        uriBuilder -> uriBuilder.queryParam("jscmd", "data")
                                .queryParam("format", "json")
                                .queryParam("bibkeys", "ISBN:" + isbn)
                                .build())
                .retrieve()
                .bodyToMono(ObjectNode.class)
                // duration is the time to wait before the next try/invocation
                .retryWhen(Retry.fixedDelay(2, Duration.ofMillis(200)))
                .block();

        assert result != null;
        JsonNode content = result.get("ISBN:" + isbn);
        return jsonNodeToBookMapper.mapJsonNodeToBook(isbn, content);

    }
}
