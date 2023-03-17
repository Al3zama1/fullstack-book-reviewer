package com.abranlezama.fullstackbookreviewer.service.book;

import com.abranlezama.fullstackbookreviewer.mapstruct.dto.BookSynchronization;
import com.abranlezama.fullstackbookreviewer.mapstruct.mapper.BookMapper;
import com.abranlezama.fullstackbookreviewer.repository.BookRepository;
import com.abranlezama.fullstackbookreviewer.mapstruct.dto.BookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService implements IBookService{

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final KafkaTemplate<String, BookSynchronization> kafkaTemplate;

    @Override
    public List<BookResponse> getAllBooks(Integer page, Integer pageSize) {
        return bookRepository.findAll(PageRequest.of(page, pageSize)).stream()
                .map(bookMapper::mapBookToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void createNewBook(String isbn) {
        kafkaTemplate.send("book-synchronization", new BookSynchronization(isbn));
    }
}
