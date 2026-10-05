package com.marketplace.hexagonal.Infrastructura.DTO.Review;

import com.marketplace.hexagonal.Domain.Review.Models.Review;

import java.math.BigDecimal;
import java.util.List;

public record ReviewSummaryResponseDTO(
        BigDecimal averageRating,
        int totalReviews,
        List<ReviewResponseDTO> reviews
) {
    public static ReviewSummaryResponseDTO from(List<Review> reviews) {
        BigDecimal avg = Review.calculateAverageRating(reviews);
        List<ReviewResponseDTO> dtos = reviews.stream()
                .map(ReviewResponseDTO::from)
                .toList();
        return new ReviewSummaryResponseDTO(avg, reviews.size(), dtos);
    }
}
