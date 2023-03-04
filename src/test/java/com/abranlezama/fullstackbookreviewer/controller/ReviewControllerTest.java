package com.abranlezama.fullstackbookreviewer.controller;

import com.abranlezama.fullstackbookreviewer.config.WebSecurityConfig;
import com.abranlezama.fullstackbookreviewer.mapstruct.dto.ReviewRequest;
import com.abranlezama.fullstackbookreviewer.mapstruct.dto.ReviewResponse;
import com.abranlezama.fullstackbookreviewer.service.review.IReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@Import(value = {WebSecurityConfig.class})
class ReviewControllerTest {

    @MockBean
    private IReviewService reviewService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private static PodamFactory podamFactory;

    @BeforeAll
    static void setUp() {
        podamFactory = new PodamFactoryImpl();
    }

    @Test
    void shouldReturnTwentyReviewsWithoutAnyOrderWhenNoParametersAreSpecified() throws Exception {
        // Given
        List<ReviewResponse> result = new ArrayList<>();
        ReviewResponse reviewResponse = podamFactory.manufacturePojo(ReviewResponse.class);
        result.add(reviewResponse);

        given(reviewService.getAllReviews(20, "none")).willReturn(result);

        // When
        mockMvc.perform(get("/api/v1/books/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", Matchers.is(1)));

        // Then
        then(reviewService).should().getAllReviews(20, "none");
    }

    @Test
    void shouldReturnSingleReview() throws Exception {
        // Given
        ReviewResponse reviewResponse = podamFactory.manufacturePojo(ReviewResponse.class);

        given(reviewService.getReviewById(reviewResponse.getBookIsbn(), reviewResponse.getReviewId())).willReturn(reviewResponse);

        // When
        // Then
        mockMvc.perform(get("/api/v1/books/{isbn}/reviews/{reviewId}", reviewResponse.getBookIsbn(),
                reviewResponse.getReviewId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId", Matchers.is(reviewResponse.getReviewId())));
    }

    @Test
    void shouldNotReturnReviewStatisticsWhenUserIsUnauthenticated() throws Exception {
        // Given

        // When
        mockMvc.perform(get("/api/v1/books/reviews/statistics"))
                .andExpect(status().isUnauthorized());

        // Then
        then(reviewService).shouldHaveNoInteractions();
    }

    @Test
    void shouldReturnReviewStatisticsWhenUserIsAuthenticated() throws Exception {
        // Given

        // When
        mockMvc.perform(get("/api/v1/books/reviews/statistics")
                .with(jwt()))
                .andExpect(status().isOk());

        // Then
        then(reviewService).should().getReviewStatistics();
    }

    @Test
    void shouldCreateNewBookReviewForAuthenticatedUserWithValidPayload() throws Exception {
        // Given
        String requestBody = """
      {
        "reviewTitle": "Great Java Book",
        "reviewContent": "I really like this book!",
        "rating": 4
      }
      """;

        given(reviewService.createBookReview(eq("42"), any(ReviewRequest.class), eq("duke"), endsWith("spring.io")))
                .willReturn(84L);

        // When
        mockMvc.perform(post("/api/v1/books/{isbn}/reviews", 42)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(jwt().jwt(builder -> builder.claim("email", "duke@spring.io")
                        .claim("preferred_username", "duke"))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", Matchers.containsString("/books/42/reviews/84")));

    }

    @Test
    void shouldRejectNewBookReviewForAuthenticatedUsersWithInvalidPayload() throws Exception {
        // Given
        String requestBody = """
      {
        "reviewContent": "I really like this book!",
        "rating": -4
      }
      """;

        // When
        mockMvc.perform(post("/api/v1/books/{isbn}/reviews", 42)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(jwt().jwt(builder -> builder.claim("email", "duke@spring.io")
                        .claim("preferred_username", "duke"))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldNotAllowDeletingReviewsWhenUserIsAuthenticatedWithoutModeratorRole() throws Exception {
        // Given
        String isbn = "42";
        long reviewId = 3;

        // When
        mockMvc.perform(delete("/api/v1/books/{isbn}/reviews/{reviewId", isbn, reviewId)
                .with(jwt()))
                .andExpect(status().isForbidden());

        // Then
        then(reviewService).shouldHaveNoInteractions();
    }

    @Test
    void shouldAllowDeletingReviewsWhenUserIsAuthenticatedAndHasModeratorRole() throws Exception {
        // Given
        String isbn = "42";
        long reviewId = 3;

        // When
        mockMvc.perform(delete("/api/v1/books/{isbn}/reviews/{reviewId}", isbn, reviewId)
                .with(user("duke").roles("moderator")))
                .andExpect(status().isNoContent());

        // Then
        then(reviewService).should().deleteReview(isbn, reviewId);
    }

}