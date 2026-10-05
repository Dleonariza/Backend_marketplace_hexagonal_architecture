package com.marketplace.hexagonal.Domain.Review.Ports.Out;

import com.marketplace.hexagonal.Domain.Review.Models.Review;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewId;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewTargetType;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;
import java.util.Optional;

public interface ReviewRepositoryPort {
    Review save(Review review);
    Optional<Review> findById(ReviewId reviewId);
    List<Review> findAllByTargetTypeAndTargetId(ReviewTargetType targetType, String targetId);
    boolean existsByAuthorIdAndTargetTypeAndTargetId(UserId authorId, ReviewTargetType targetType, String targetId);
    void deleteById(ReviewId reviewId);
}
