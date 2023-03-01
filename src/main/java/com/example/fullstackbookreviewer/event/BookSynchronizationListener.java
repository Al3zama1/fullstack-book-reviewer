package com.example.fullstackbookreviewer.event;

import com.example.fullstackbookreviewer.client.OpenLibraryApiClient;
import com.example.fullstackbookreviewer.entity.Book;
import com.example.fullstackbookreviewer.mapstruct.dto.BookSynchronization;
import com.example.fullstackbookreviewer.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookSynchronizationListener {
    private final BookRepository bookRepository;
    private final OpenLibraryApiClient openLibraryApiClient;

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
