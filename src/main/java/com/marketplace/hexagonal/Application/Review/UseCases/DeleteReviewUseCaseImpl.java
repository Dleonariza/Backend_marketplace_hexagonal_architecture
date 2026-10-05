package com.marketplace.hexagonal.Application.Review.UseCases;

import com.marketplace.hexagonal.Domain.Review.Exceptions.ReviewAccessDeniedException;
import com.marketplace.hexagonal.Domain.Review.Exceptions.ReviewNotFoundException;
import com.marketplace.hexagonal.Domain.Review.Models.Review;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewId;
import com.marketplace.hexagonal.Domain.Review.Ports.In.DeleteReviewUseCase;
import com.marketplace.hexagonal.Domain.Review.Ports.Out.ReviewRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteReviewUseCaseImpl implements DeleteReviewUseCase {

    private final ReviewRepositoryPort reviewRepo;

    @Override
    public void deleteReview(ReviewId reviewId, UserId requesterId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (!review.getAuthorId().equals(requesterId))
            throw new ReviewAccessDeniedException(reviewId);

        reviewRepo.deleteById(reviewId);
    }
}
