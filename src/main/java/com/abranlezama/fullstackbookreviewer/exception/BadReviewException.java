package com.abranlezama.fullstackbookreviewer.exception;

public class BadReviewException extends RuntimeException{
    public BadReviewException(String message) {
        super(message);
    }
}
