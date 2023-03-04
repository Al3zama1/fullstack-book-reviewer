package com.abranlezama.fullstackbookreviewer.service.review;

public interface IReviewVerifier {
    boolean doesMeetQualityStandards(String review);
}
