package com.example.fullstackbookreviewer.mapstruct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private long reviewId;
    private String reviewContent;
    private String reviewTitle;
    private int rating;
    private String bookIsbn;
    private String bookTitle;
    private String bookThumbnailUrl;
    private String submittedBy;
    private LocalDateTime submittedAt;
}
