package com.abranlezama.fullstackbookreviewer.mapstruct.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ReviewRequest {
    @NotEmpty
    private String reviewTitle;
    @NotEmpty
    private String reviewContent;
    @NotNull
    @PositiveOrZero
    private int rating;
}
