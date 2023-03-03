package com.example.fullstackbookreviewer.service.review;

import com.example.fullstackbookreviewer.entity.Book;
import com.example.fullstackbookreviewer.entity.Review;
import com.example.fullstackbookreviewer.entity.User;
import com.example.fullstackbookreviewer.exception.BadReviewException;
import com.example.fullstackbookreviewer.exception.BookNotFoundException;
import com.example.fullstackbookreviewer.exception.ExceptionMessages;
import com.example.fullstackbookreviewer.exception.ReviewNotFoundException;
import com.example.fullstackbookreviewer.mapstruct.dto.ReviewRequest;
import com.example.fullstackbookreviewer.mapstruct.dto.ReviewResponse;
import com.example.fullstackbookreviewer.mapstruct.mapper.ReviewMapper;
import com.example.fullstackbookreviewer.repository.BookRepository;
import com.example.fullstackbookreviewer.repository.ReviewRepository;
import com.example.fullstackbookreviewer.service.user.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private IReviewVerifier reviewVerifier;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    IUserService userService;
    @Mock
    private ReviewRepository reviewRepository;
    @InjectMocks
    private ReviewService cut;
    private static final String EMAIL = "duke@spring.io";
    private static final String USERNAME = "duke";
    private static final String ISBN = "42";

    @Test
    void shouldThrowExceptionWhenReviewedBookIsNotExisting() {
        // Given
        given(bookRepository.findByIsbn(ISBN)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.createBookReview(ISBN, null, USERNAME, EMAIL))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage(ExceptionMessages.BOOK_NOT_FOUND);

        // Then
        then(reviewRepository).shouldHaveNoInteractions();
    }

    @Test
    void shouldRejectReviewWhenReviewQualityIsBad() {
        // Given
        ReviewRequest request = new ReviewRequest("Title", "bad content", 1);

        given(bookRepository.findByIsbn(ISBN)).willReturn(Optional.of(new Book()));
        given(reviewVerifier.doesMeetQualityStandards(request.getReviewContent())).willReturn(false);

        // When
        assertThatThrownBy(() -> cut.createBookReview(ISBN, request, USERNAME, EMAIL))
                .isInstanceOf(BadReviewException.class)
                .hasMessage(ExceptionMessages.BAD_REVIEW);

        // Then
        then(reviewRepository).shouldHaveNoInteractions();
    }

    @Test
    void shouldStoreReviewWhenReviewQualityIsGoodAndBookIsPresent() {
        // Given
        ReviewRequest reviewRequest = new ReviewRequest(
                "Title", "good content", 1);

        given(bookRepository.findByIsbn(ISBN)).willReturn(Optional.of(new Book()));
        given(reviewVerifier.doesMeetQualityStandards(reviewRequest.getReviewContent())).willReturn(true);
        given(userService.getOrCreateUser(USERNAME, EMAIL)).willReturn(new User());
        given(reviewRepository.save(any(Review.class))).willAnswer(invocation -> {
            Review savedReview = invocation.getArgument(0);
            savedReview.setId(42L);
            return savedReview;
        });

        // When
        Long result = cut.createBookReview(ISBN, reviewRequest, USERNAME, EMAIL);

        // Then
        assertThat(result).isEqualTo(42);
    }

    @Test
    void shouldThrowReviewNotFoundExceptionWhenNoReviewExists() {
        // Given
        long reviewId = 1L;
        given(reviewRepository.findByIdAndBookIsbn(reviewId, ISBN)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.getReviewById(ISBN, reviewId))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessage(ExceptionMessages.REVIEW_NOT_FOUND);

        // Then
        then(reviewMapper).shouldHaveNoInteractions();
    }

    @Test
    void shouldReturnTopFiveReviewsByRating() {
        // Given
        String orderBy = "rating";
        int size = 20;

        // When
        cut.getAllReviews(size, orderBy);

        // Then
        then(reviewRepository).should().findTop5ByOrderByRatingDescCreatedAtDesc();
    }

    @Test
    void shouldReturnTwentyReviews() {
        // Given
        String orderBy = "none";
        int size = 20;

        // When
        cut.getAllReviews(size, orderBy);

        // Then
        then(reviewRepository).should().findAllByOrderByCreatedAtDesc(any(PageRequest.class));
    }
}