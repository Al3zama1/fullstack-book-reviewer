package com.example.fullstackbookreviewer.service.book;

import com.example.fullstackbookreviewer.mapstruct.dto.BookSynchronization;
import com.example.fullstackbookreviewer.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Profile("default")
@RequiredArgsConstructor
@Slf4j
public class InitialBookCreator {
    private final BookRepository bookRepository;
    private final KafkaTemplate<String, BookSynchronization> kafkaTemplate;

    @EventListener
    public void initialize(ApplicationReadyEvent event) {
        log.info("InitialBookCreator running ...");
        if (bookRepository.count() == 0) {
            log.info("Going to initialize first set of books");
            for (String isbn : List.of("9780321751041", "9780321160768", "9780596004651")) {
                kafkaTemplate.send("book-synchronization", new BookSynchronization(isbn));
                // enforce uniqueness of messages as messages might get stuck in the mock SQS queue otheriwse
            }
        } else {
            log.info("No need to pre-populate books as database already contains some");
        }
    }
}
