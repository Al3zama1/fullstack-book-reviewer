package com.example.fullstackbookreviewer.service.review;

import com.example.fullstackbookreviewer.entity.Book;
import com.example.fullstackbookreviewer.entity.Review;
import com.example.fullstackbookreviewer.exception.BadReviewException;
import com.example.fullstackbookreviewer.exception.BookNotFoundException;
import com.example.fullstackbookreviewer.exception.ExceptionMessages;
import com.example.fullstackbookreviewer.exception.ReviewNotFoundException;
import com.example.fullstackbookreviewer.mapstruct.dto.ReviewRequest;
import com.example.fullstackbookreviewer.mapstruct.dto.ReviewResponse;
import com.example.fullstackbookreviewer.mapstruct.dto.ReviewStatisticResponse;
import com.example.fullstackbookreviewer.mapstruct.mapper.ReviewMapper;
import com.example.fullstackbookreviewer.mapstruct.mapper.ReviewStatisticMapper;
import com.example.fullstackbookreviewer.repository.BookRepository;
import com.example.fullstackbookreviewer.repository.ReviewRepository;
import com.example.fullstackbookreviewer.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService implements IReviewService{
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final IUserService userService;
    private final IReviewVerifier reviewVerifier;
    private final ReviewMapper reviewMapper;
    private final ReviewStatisticMapper reviewStatisticMapper;
    @Override
    public Long createBookReview(String isbn, ReviewRequest request, String username, String email) {
        Optional<Book> bookOptional = bookRepository.findByIsbn(isbn);

        if (bookOptional.isEmpty()) throw new BookNotFoundException(ExceptionMessages.BOOK_NOT_FOUND);

        if (!reviewVerifier.doesMeetQualityStandards(request.getReviewContent())) {
            throw new BadReviewException(ExceptionMessages.BAD_REVIEW);
        }

        Review review = Review.builder()
                .book(bookOptional.get())
                .content(request.getReviewContent())
                .title(request.getReviewTitle())
                .rating(request.getRating())
                .user(userService.getOrCreateUser(username, email))
                .build();

        review = reviewRepository.save(review);
        return review.getId();
    }

    @Override
    public List<ReviewStatisticResponse> getReviewStatistics() {
        return reviewRepository.getReviewStatistics()
                .stream().map(reviewStatisticMapper::mapReviewStatisticToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReview(String isbn, long reviewId) {
        reviewRepository.deleteByIdAndBookIsbn(reviewId, isbn);
    }

    @Override
    public ReviewResponse getReviewById(String isbn, long reviewId) {
        Optional<Review> reviewOptional = reviewRepository.findByIdAndBookIsbn(reviewId, isbn);

        if (reviewOptional.isEmpty()) throw new ReviewNotFoundException(ExceptionMessages.REVIEW_NOT_FOUND);

        return reviewMapper.mapReviewToDto(reviewOptional.get());
    }

    @Override
    public List<ReviewResponse> getAllReviews(Integer size, String orderBy) {
        List<Review> reviews;

        if (orderBy.equals("rating")) {
            reviews = reviewRepository.findTop5ByOrderByRatingDescCreatedAtDesc();
        } else {
            reviews = reviewRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, size));
        }

        return reviews.stream()
                .map(reviewMapper::mapReviewToDto)
                .collect(Collectors.toList());
    }
}
