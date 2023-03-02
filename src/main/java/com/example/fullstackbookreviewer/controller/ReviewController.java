package com.example.fullstackbookreviewer.controller;

import com.example.fullstackbookreviewer.mapstruct.dto.ReviewRequest;
import com.example.fullstackbookreviewer.mapstruct.dto.ReviewResponse;
import com.example.fullstackbookreviewer.mapstruct.dto.ReviewStatisticResponse;
import com.example.fullstackbookreviewer.service.review.IReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class ReviewController {
    private final IReviewService reviewService;

    @GetMapping("/reviews")
    public List<ReviewResponse> getAllReviews(@RequestParam(name = "size", defaultValue = "20") Integer size,
                                              @RequestParam(name = "orderBy", defaultValue = "none") String orderBy) {
        return reviewService.getAllReviews(size, orderBy);
    }

    @GetMapping("/{isbn}/reviews/{reviewId}")
    public ReviewResponse getReviewById(@PathVariable String isbn, @PathVariable Long reviewId) {
        return reviewService.getReviewById(isbn, reviewId);
    }

    @GetMapping("/reviews/statistics")
    public List<ReviewStatisticResponse> getReviewStatistics() {
        return reviewService.getReviewStatistics();
    }

    @PostMapping("/{isbn}/reviews")
    public ResponseEntity<Void> createBookReview(@PathVariable String isbn,
                                                 @RequestBody @Valid ReviewRequest request,
                                                 JwtAuthenticationToken jwt,
                                                 UriComponentsBuilder uriComponentsBuilder) {
        Long reviewId = reviewService.createBookReview(isbn, request,
                jwt.getTokenAttributes().get("preferred_username").toString(),
                jwt.getTokenAttributes().get("email").toString());

        UriComponents uriComponents = uriComponentsBuilder.path("/api/v1/books/{isbn}/reviews/{reviewId}")
                .buildAndExpand(isbn, reviewId);
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @DeleteMapping("/{isbn}/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookReview(@PathVariable String isbn, @PathVariable Long reviewId) {
        reviewService.deleteReview(isbn, reviewId);
    }
}
