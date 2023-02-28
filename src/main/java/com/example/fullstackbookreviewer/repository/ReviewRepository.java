package com.example.fullstackbookreviewer.repository;

import com.example.fullstackbookreviewer.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(value =
        """
        SELECT id, ratings, isbn, avg
        FROM book
        INNER JOIN (SELECT book_id, ROUND(AVG(rating), 2) avg, COUNT(*) ratings FROM review group by book_id) AS statistics
        ON statistics.book_id = id;
        """, nativeQuery = true)
    List<ReviewStatistic> getReviewStatistics();
    List<Review> findTop5ByOrderByRatingDescCreatedAtDesc();
    List<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);

    void deleteByIdAndBookIsbn(Long reviewId, String isbn);

    Optional<Review> findByIdAndBookIsbn(Long reviewId, String isbn);
}
