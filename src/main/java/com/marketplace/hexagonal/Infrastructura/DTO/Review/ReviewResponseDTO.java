package com.marketplace.hexagonal.Infrastructura.DTO.Review;

import com.marketplace.hexagonal.Domain.Review.Models.Review;

import java.time.Instant;

public record ReviewResponseDTO(
        String id,
        String authorId,
        String authorName,
        int rating,
        String comment,
        Instant createdAt
) {
    public static ReviewResponseDTO from(Review review) {
        return new ReviewResponseDTO(
                review.getReviewId().getValue(),
                review.getAuthorId().getValue(),
                review.getAuthorName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
