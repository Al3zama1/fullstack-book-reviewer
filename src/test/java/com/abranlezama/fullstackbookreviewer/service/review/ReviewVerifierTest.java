package com.abranlezama.fullstackbookreviewer.service.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewVerifierTest {
    private ReviewVerifier cut;

    @BeforeEach
    void setUp() {
        this.cut = new ReviewVerifier();
    }

    @Test
    void shouldFailWhenReviewContainsSwearWord() {
        // Given
        String review = "This book is shit";

        // When
        boolean result = cut.doesMeetQualityStandards(review);
        assertThat(result).withFailMessage("ReviewVerifier did not detect swear words").isFalse();
    }

    @Test
    void shouldFailWhenReviewContainsLoremIpsum() {
        // Given
        String review = "Lorem ipsum is placeholder text commonly " +
                "used in the graphic, print, and publishing industries for " +
                "previewing layouts and visual mockups";

        // When
        boolean result = cut.doesMeetQualityStandards(review);

        // Then
        assertThat(result).withFailMessage("ReviewVerifier did not detect lorem ipsum").isFalse();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/badReview.csv")
    void shouldFailWhenReviewIsOfBadQuality(String review) {
        // Given -> reviews given through csv file

        // When
        boolean result = cut.doesMeetQualityStandards(review);

        // Then
        assertThat(result).withFailMessage("ReviewVerifier did not detect lorem ipsum").isFalse();
    }

    @Test
    void shouldPassWhenReviewIsGood() {
        // Given
        String review = "I can totally recommend this book who is interested in learning" +
                "how to write Java code?";

        // When
        boolean result = cut.doesMeetQualityStandards(review);

        // Then
        assertThat(result).withFailMessage("ReviewVerifier did not pass a good review").isTrue();
    }

}