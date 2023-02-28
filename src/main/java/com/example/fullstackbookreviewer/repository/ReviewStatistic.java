package com.example.fullstackbookreviewer.repository;

import java.math.BigDecimal;

public interface ReviewStatistic {
    Long getId();
    Long getRatings();
    String getIsbn();
    BigDecimal getAvg();
}
