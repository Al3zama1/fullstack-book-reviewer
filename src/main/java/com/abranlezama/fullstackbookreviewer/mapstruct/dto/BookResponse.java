package com.abranlezama.fullstackbookreviewer.mapstruct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponse {
    private String title;
    private String isbn;
    private String author;
    private String genre;
    private String thumbnailUrl;
    private String description;
    private String publisher;
    private Long pages;

}
