package com.marketplace.hexagonal.Domain.Review.Ports.In;

import com.marketplace.hexagonal.Domain.Review.Models.ReviewId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface DeleteReviewUseCase {

    void deleteReview(ReviewId reviewId, UserId requesterId);
}
