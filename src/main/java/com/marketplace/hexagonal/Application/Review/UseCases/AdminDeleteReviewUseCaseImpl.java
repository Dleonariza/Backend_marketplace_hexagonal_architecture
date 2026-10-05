package com.marketplace.hexagonal.Application.Review.UseCases;

import com.marketplace.hexagonal.Domain.Review.Exceptions.ReviewNotFoundException;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewId;
import com.marketplace.hexagonal.Domain.Review.Ports.In.AdminDeleteReviewUseCase;
import com.marketplace.hexagonal.Domain.Review.Ports.Out.ReviewRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminDeleteReviewUseCaseImpl implements AdminDeleteReviewUseCase {

    private final ReviewRepositoryPort reviewRepo;

    @Override
    public void deleteReview(ReviewId reviewId) {
        if (reviewRepo.findById(reviewId).isEmpty())
            throw new ReviewNotFoundException(reviewId);

        reviewRepo.deleteById(reviewId);
    }
}
