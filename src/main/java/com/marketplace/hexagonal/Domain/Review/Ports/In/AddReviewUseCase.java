package com.marketplace.hexagonal.Domain.Review.Ports.In;

import com.marketplace.hexagonal.Domain.Review.Models.Review;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewTargetType;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface AddReviewUseCase {
    Review addReview(UserId authorId, String authorName,
                     ReviewTargetType targetType, String targetId,
                     int rating, String comment);
}
