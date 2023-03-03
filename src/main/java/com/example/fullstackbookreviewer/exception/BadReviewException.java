package com.example.fullstackbookreviewer.exception;

public class BadReviewException extends RuntimeException{
    public BadReviewException(String message) {
        super(message);
    }
}
