package com.abranlezama.fullstackbookreviewer.service.review;

import com.abranlezama.fullstackbookreviewer.mapstruct.dto.ReviewStatisticResponse;
import com.abranlezama.fullstackbookreviewer.mapstruct.dto.ReviewRequest;
import com.abranlezama.fullstackbookreviewer.mapstruct.dto.ReviewResponse;

import java.util.List;

public interface IReviewService {
    Long createBookReview(String isbn, ReviewRequest request, String username, String email);
    List<ReviewStatisticResponse> getReviewStatistics();
    void deleteReview(String isbn, long reviewId);
    ReviewResponse getReviewById(String isbn, long reviewId);
    List<ReviewResponse> getAllReviews(Integer size, String orderBy);
}
