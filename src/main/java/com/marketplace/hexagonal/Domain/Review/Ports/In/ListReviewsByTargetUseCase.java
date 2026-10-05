package com.marketplace.hexagonal.Domain.Review.Ports.In;

import com.marketplace.hexagonal.Domain.Review.Models.Review;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewTargetType;

import java.util.List;

public interface ListReviewsByTargetUseCase {
    List<Review> listReviews(ReviewTargetType targetType, String targetId);
}
