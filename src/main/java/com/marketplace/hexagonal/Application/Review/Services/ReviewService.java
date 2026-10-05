package com.marketplace.hexagonal.Application.Review.Services;

import com.marketplace.hexagonal.Domain.Review.Models.Review;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewId;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewTargetType;
import com.marketplace.hexagonal.Domain.Review.Ports.In.AddReviewUseCase;
import com.marketplace.hexagonal.Domain.Review.Ports.In.AdminDeleteReviewUseCase;
import com.marketplace.hexagonal.Domain.Review.Ports.In.DeleteReviewUseCase;
import com.marketplace.hexagonal.Domain.Review.Ports.In.ListReviewsByTargetUseCase;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ReviewService {

    private final AddReviewUseCase addReviewUseCase;
    private final ListReviewsByTargetUseCase listReviewsByTargetUseCase;
    private final DeleteReviewUseCase deleteReviewUseCase;
    private final AdminDeleteReviewUseCase adminDeleteReviewUseCase;

    public Review addProductReview(UserId authorId, String authorName,
                                   String productId, int rating, String comment) {
        return addReviewUseCase.addReview(
                authorId, authorName, ReviewTargetType.PRODUCT, productId, rating, comment);
    }

    public Review addSellerReview(UserId authorId, String authorName,
                                  String sellerId, int rating, String comment) {
        return addReviewUseCase.addReview(
                authorId, authorName, ReviewTargetType.SELLER, sellerId, rating, comment);
    }

    public List<Review> listProductReviews(String productId) {
        return listReviewsByTargetUseCase.listReviews(ReviewTargetType.PRODUCT, productId);
    }

    public List<Review> listSellerReviews(String sellerId) {
        return listReviewsByTargetUseCase.listReviews(ReviewTargetType.SELLER, sellerId);
    }

    public void deleteReview(ReviewId reviewId, UserId requesterId) {
        deleteReviewUseCase.deleteReview(reviewId, requesterId);
    }

    public void adminDeleteReview(ReviewId reviewId) {
        adminDeleteReviewUseCase.deleteReview(reviewId);
    }
}
