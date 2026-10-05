package com.marketplace.hexagonal.Application.Review.UseCases;

import com.marketplace.hexagonal.Domain.Review.Models.Review;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewTargetType;
import com.marketplace.hexagonal.Domain.Review.Ports.In.ListReviewsByTargetUseCase;
import com.marketplace.hexagonal.Domain.Review.Ports.Out.ReviewRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListReviewsByTargetUseCaseImpl implements ListReviewsByTargetUseCase {

    private final ReviewRepositoryPort reviewRepo;

    @Override
    public List<Review> listReviews(ReviewTargetType targetType, String targetId) {
        return reviewRepo.findAllByTargetTypeAndTargetId(targetType, targetId);
    }
}
