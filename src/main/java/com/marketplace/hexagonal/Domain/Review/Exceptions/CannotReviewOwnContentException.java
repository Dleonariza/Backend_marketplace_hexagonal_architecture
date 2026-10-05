package com.marketplace.hexagonal.Domain.Review.Exceptions;

public class CannotReviewOwnContentException extends RuntimeException {
    public CannotReviewOwnContentException() {
        super("You cannot review your own content.");
    }
}
