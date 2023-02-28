package com.example.fullstackbookreviewer.mapstruct.mapper;

import com.example.fullstackbookreviewer.entity.Review;
import com.example.fullstackbookreviewer.mapstruct.dto.ReviewResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewMapperTest {

    private static ReviewMapper reviewMapper;
    private static PodamFactory podamFactory;

    @BeforeAll()
    static void setUP() {
        reviewMapper = new ReviewMapperImpl();
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    void testReviewIsMappedCorrectly() {
        // Given
        Review review = podamFactory.manufacturePojo(Review.class);

        // When
        ReviewResponse reviewResponse = reviewMapper.mapReviewToDto(review);

        // Then
        assertThat(reviewResponse.getReviewId()).isEqualTo(review.getId());
        assertThat(reviewResponse.getReviewContent()).isEqualTo(review.getContent());
        assertThat(reviewResponse.getReviewTitle()).isEqualTo(review.getTitle());
        assertThat(reviewResponse.getRating()).isEqualTo(review.getRating());
        assertThat(reviewResponse.getBookIsbn()).isEqualTo(review.getBook().getIsbn());
        assertThat(reviewResponse.getBookTitle()).isEqualTo(review.getBook().getTitle());
        assertThat(reviewResponse.getBookThumbnailUrl()).isEqualTo(review.getBook().getThumbnailUrl());
        assertThat(reviewResponse.getSubmittedBy()).isEqualTo(review.getUser().getName());
        assertThat(reviewResponse.getSubmittedAt()).isEqualTo(review.getCreatedAt());


    }

}