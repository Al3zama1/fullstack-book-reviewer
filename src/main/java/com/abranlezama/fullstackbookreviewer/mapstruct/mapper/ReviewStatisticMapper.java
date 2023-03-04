package com.abranlezama.fullstackbookreviewer.mapstruct.mapper;

import com.abranlezama.fullstackbookreviewer.mapstruct.dto.ReviewStatisticResponse;
import com.abranlezama.fullstackbookreviewer.repository.ReviewStatistic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ReviewStatisticMapper {

    @Mapping(source = "id", target = "bookId")
    ReviewStatisticResponse mapReviewStatisticToDto(ReviewStatistic reviewStatistic);
}
