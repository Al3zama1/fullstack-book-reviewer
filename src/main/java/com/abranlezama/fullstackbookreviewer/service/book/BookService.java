package com.abranlezama.fullstackbookreviewer.service.book;

import com.abranlezama.fullstackbookreviewer.mapstruct.mapper.BookMapper;
import com.abranlezama.fullstackbookreviewer.repository.BookRepository;
import com.abranlezama.fullstackbookreviewer.mapstruct.dto.BookResponse;
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
