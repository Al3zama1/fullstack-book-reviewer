package com.example.fullstackbookreviewer.service.book;

import com.example.fullstackbookreviewer.mapstruct.dto.BookResponse;

import java.util.List;

public interface IBookService {
    List<BookResponse> getAllBooks(Integer page, Integer pageSize);
}
