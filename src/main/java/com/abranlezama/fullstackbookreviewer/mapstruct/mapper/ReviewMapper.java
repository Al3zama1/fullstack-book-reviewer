package com.abranlezama.fullstackbookreviewer.mapstruct.mapper;

import com.abranlezama.fullstackbookreviewer.entity.Review;
import com.abranlezama.fullstackbookreviewer.mapstruct.dto.ReviewResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ReviewMapper {
    @Mapping(target = "reviewId", source = "id")
    @Mapping(target = "reviewContent", source = "content")
    @Mapping(target = "reviewTitle", source = "title")
    @Mapping(target = "bookIsbn", source = "book.isbn")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "bookThumbnailUrl", source = "book.thumbnailUrl")
    @Mapping(target = "submittedBy", source = "user.name")
    @Mapping(target = "submittedAt", source = "createdAt")
    ReviewResponse mapReviewToDto(Review review);
}
