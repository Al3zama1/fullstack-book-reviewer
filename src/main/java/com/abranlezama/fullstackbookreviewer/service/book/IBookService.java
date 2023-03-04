package com.abranlezama.fullstackbookreviewer.service.book;

import com.abranlezama.fullstackbookreviewer.mapstruct.dto.BookResponse;

import java.util.List;

public interface IBookService {
    List<BookResponse> getAllBooks(Integer page, Integer pageSize);
}
