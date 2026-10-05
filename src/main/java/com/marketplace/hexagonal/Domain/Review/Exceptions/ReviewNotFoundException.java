package com.marketplace.hexagonal.Domain.Review.Exceptions;

import com.marketplace.hexagonal.Domain.Review.Models.ReviewId;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(ReviewId reviewId) {
        super("Review not found: " + reviewId.getValue());
    }
}
