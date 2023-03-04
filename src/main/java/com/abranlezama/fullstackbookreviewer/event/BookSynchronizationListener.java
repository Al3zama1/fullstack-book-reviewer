package com.abranlezama.fullstackbookreviewer.event;

import com.abranlezama.fullstackbookreviewer.client.OpenLibraryApiClient;
import com.abranlezama.fullstackbookreviewer.entity.Book;
import com.abranlezama.fullstackbookreviewer.mapstruct.dto.BookSynchronization;
import com.abranlezama.fullstackbookreviewer.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookSynchronizationListener {
    private final BookRepository bookRepository;
    private final OpenLibraryApiClient openLibraryApiClient;

    @KafkaListener(
            topics = "book-synchronization",
            groupId = "myGroup",
            containerFactory = "userKafkaListenerContainerFactory"
    )
    public void consumeBookUpdates(BookSynchronization bookSynchronization) {
        String isbn = bookSynchronization.getIsbn();
        log.info("Incoming book update for isbn '{}'", isbn);

        if (isbn.length() != 13) {
            log.warn("Incoming isbn for book is not 13 characters long, rejecting it");
            return;
        }

        if (bookRepository.findByIsbn(isbn).isPresent()) {
            log.debug("Book with isbn '{}' is already present, rejecting it", isbn);
            return;
        }

        Book book = openLibraryApiClient.fetchMetadataForBook(isbn);
        book = bookRepository.save(book);

        log.info("Successfully stored new book '{}'", book);
    }
}
