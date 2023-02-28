package com.example.fullstackbookreviewer.mapstruct.mapper;

import com.example.fullstackbookreviewer.entity.Book;
import com.example.fullstackbookreviewer.mapstruct.dto.BookResponse;
import org.mapstruct.Mapper;

@Mapper
public interface BookMapper {
    BookResponse mapBookToDto(Book book);
}
