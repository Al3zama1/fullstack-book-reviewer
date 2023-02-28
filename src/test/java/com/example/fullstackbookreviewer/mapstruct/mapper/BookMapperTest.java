package com.example.fullstackbookreviewer.mapstruct.mapper;

import com.example.fullstackbookreviewer.entity.Book;
import com.example.fullstackbookreviewer.mapstruct.dto.BookResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {
    private static BookMapper bookMapper;
    private static PodamFactory podamFactory;

    @BeforeAll
    static void setUp() {
        bookMapper = new BookMapperImpl();
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    void testBookIsMappedCorrectlyToDto() {
        // Given
        Book book = podamFactory.manufacturePojo(Book.class);

        // When
        BookResponse bookResponse = bookMapper.mapBookToDto(book);

        // Then
        assertThat(book.getTitle()).isEqualTo(bookResponse.getTitle());
        assertThat(book.getIsbn()).isEqualTo(bookResponse.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(bookResponse.getAuthor());
        assertThat(book.getGenre()).isEqualTo(bookResponse.getGenre());
        assertThat(book.getThumbnailUrl()).isEqualTo(bookResponse.getThumbnailUrl());
        assertThat(book.getDescription()).isEqualTo(bookResponse.getDescription());
        assertThat(book.getPublisher()).isEqualTo(bookResponse.getPublisher());
        assertThat(book.getPages()).isEqualTo(bookResponse.getPages());
    }

}