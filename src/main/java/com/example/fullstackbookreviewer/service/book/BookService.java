package com.example.fullstackbookreviewer.service.book;

import com.example.fullstackbookreviewer.mapstruct.dto.BookResponse;
import com.example.fullstackbookreviewer.mapstruct.mapper.BookMapper;
import com.example.fullstackbookreviewer.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService implements IBookService{

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public List<BookResponse> getAllBooks(Integer page, Integer pageSize) {
        return bookRepository.findAll(PageRequest.of(page, pageSize)).stream()
                .map(bookMapper::mapBookToDto)
                .collect(Collectors.toList());
    }
}
