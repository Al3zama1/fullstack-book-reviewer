package com.abranlezama.fullstackbookreviewer.mapstruct.mapper;

import com.abranlezama.fullstackbookreviewer.entity.Book;
import com.abranlezama.fullstackbookreviewer.mapstruct.dto.BookResponse;
import org.mapstruct.Mapper;

@Mapper
public interface BookMapper {
    BookResponse mapBookToDto(Book book);
}
