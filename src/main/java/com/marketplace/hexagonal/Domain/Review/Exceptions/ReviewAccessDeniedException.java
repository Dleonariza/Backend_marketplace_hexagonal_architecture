package com.marketplace.hexagonal.Domain.Review.Exceptions;

import com.marketplace.hexagonal.Domain.Review.Models.ReviewId;

public class ReviewAccessDeniedException extends RuntimeException {
    public ReviewAccessDeniedException(ReviewId reviewId) {
        super("You do not have permission to delete review: " + reviewId.getValue());
    }
}
