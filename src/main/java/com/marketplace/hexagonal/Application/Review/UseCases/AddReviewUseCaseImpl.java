package com.marketplace.hexagonal.Application.Review.UseCases;

import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Review.Exceptions.CannotReviewOwnContentException;
import com.marketplace.hexagonal.Domain.Review.Exceptions.ReviewAlreadyExistsException;
import com.marketplace.hexagonal.Domain.Review.Models.Review;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewTargetType;
import com.marketplace.hexagonal.Domain.Review.Ports.In.AddReviewUseCase;
import com.marketplace.hexagonal.Domain.Review.Ports.Out.ReviewRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddReviewUseCaseImpl implements AddReviewUseCase {

    private final ReviewRepositoryPort reviewRepo;
    private final ProductRepositoryPort productRepo;
    private final SellerProfileRepositoryPort sellerProfileRepo;

    @Override
    public Review addReview(UserId authorId,
                            String authorName,
                            ReviewTargetType targetType,
                            String targetId,
                            int rating,
                            String comment) {
        if (targetType == ReviewTargetType.PRODUCT) {
            productRepo.findById(ProductId.of(targetId))
                    .orElseThrow(() -> new ProductNotFoundException(ProductId.of(targetId)));
        } else {
            sellerProfileRepo.findByUserId(UserId.of(targetId))
                    .orElseThrow(() -> new SellerProfileNotFoundException(
                            "Seller not found: " + targetId));

            if (targetId.equals(authorId.getValue()))
                throw new CannotReviewOwnContentException();
        }

        if (reviewRepo.existsByAuthorIdAndTargetTypeAndTargetId(authorId, targetType, targetId))
            throw new ReviewAlreadyExistsException(authorId, targetType, targetId);

        return reviewRepo.save(
                Review.create(authorId, authorName, targetType, targetId, rating, comment));
    }
}
