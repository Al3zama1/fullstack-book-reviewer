package com.abranlezama.fullstackbookreviewer.mapstruct.mapper;

import com.abranlezama.fullstackbookreviewer.repository.ReviewRepository;
import com.abranlezama.fullstackbookreviewer.repository.ReviewStatistic;
import com.abranlezama.fullstackbookreviewer.mapstruct.dto.ReviewStatisticResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReviewStatisticMapperTest {

    @Autowired
    private ReviewRepository reviewRepository;
    private static ReviewStatisticMapper cut;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.1")
            .withDatabaseName("book_reviewer")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:p6spy:postgresql://" +
                postgreSQLContainer.getHost() + ":" + postgreSQLContainer.getFirstMappedPort() + "/" +
                postgreSQLContainer.getDatabaseName());
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @BeforeAll
    static void setUp() {
        cut = new ReviewStatisticMapperImpl();
    }

    @Test
    @Sql("/scripts/INIT_REVIEW_EACH_BOOK.sql")
    void testReviewStatisticIsMappedCorrectlyToDto() {
        // Given
        List<ReviewStatistic> reviewStatistics = reviewRepository.getReviewStatistics();
        ReviewStatistic reviewStatistic = reviewStatistics.get(0);
        // When
        ReviewStatisticResponse reviewStatisticResponse = cut.mapReviewStatisticToDto(reviewStatistic);

        // Then
        assertThat(reviewStatisticResponse.getBookId()).isEqualTo(reviewStatistic.getId());
        assertThat(reviewStatisticResponse.getIsbn()).isEqualTo(reviewStatistic.getIsbn());
        assertThat(reviewStatisticResponse.getRatings()).isEqualTo(reviewStatistic.getRatings());
        assertThat(reviewStatisticResponse.getAvg()).isEqualTo(reviewStatistic.getAvg());

    }

}