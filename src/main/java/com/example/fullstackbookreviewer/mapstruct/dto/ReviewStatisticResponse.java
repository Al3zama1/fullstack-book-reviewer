package com.example.fullstackbookreviewer.mapstruct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewStatisticResponse {
    private long bookId;
    private String isbn;
    private BigDecimal avg;
    private long ratings;
}
