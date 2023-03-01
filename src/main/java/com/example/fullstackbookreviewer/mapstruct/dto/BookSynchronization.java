package com.example.fullstackbookreviewer.mapstruct.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BookSynchronization {
    private String isbn;

    @JsonCreator
    public BookSynchronization(@JsonProperty("isbn") String isbn) {
        this.isbn = isbn;
    }
}
