package com.marketplace.hexagonal.Domain.Review.Ports.In;

import com.marketplace.hexagonal.Domain.Review.Models.ReviewId;

public interface AdminDeleteReviewUseCase {

    void deleteReview(ReviewId reviewId);
}
