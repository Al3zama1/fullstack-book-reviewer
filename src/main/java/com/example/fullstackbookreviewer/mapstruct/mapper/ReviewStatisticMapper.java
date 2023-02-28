package com.example.fullstackbookreviewer.mapstruct.mapper;

import com.example.fullstackbookreviewer.mapstruct.dto.ReviewStatisticResponse;
import com.example.fullstackbookreviewer.repository.ReviewStatistic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ReviewStatisticMapper {

    @Mapping(source = "id", target = "bookId")
    ReviewStatisticResponse mapReviewStatisticToDto(ReviewStatistic reviewStatistic);
}
