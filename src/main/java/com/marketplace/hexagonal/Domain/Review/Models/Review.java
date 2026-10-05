package com.marketplace.hexagonal.Domain.Review.Models;

import com.marketplace.hexagonal.Domain.Review.Exceptions.InvalidRatingException;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Review {

    private ReviewId reviewId;
    private UserId authorId;

    private String authorName;
    private ReviewTargetType targetType;

    private String targetId;

    private int rating;
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;

    public static Review create(UserId authorId,
                                String authorName,
                                ReviewTargetType targetType,
                                String targetId,
                                int rating,
                                String comment) {
        if (rating < 1 || rating > 5)
            throw new InvalidRatingException(rating);

        Review r = new Review();
        r.reviewId = ReviewId.generate();
        r.authorId = authorId;
        r.authorName = authorName;
        r.targetType = targetType;
        r.targetId = targetId;
        r.rating = rating;
        r.comment = comment;
        r.createdAt = Instant.now();
        r.updatedAt = Instant.now();
        return r;
    }

    public static BigDecimal calculateAverageRating(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty())
            return BigDecimal.ZERO;

        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        return BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);
    }
}
