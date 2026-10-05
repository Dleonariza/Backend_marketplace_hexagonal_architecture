package com.marketplace.hexagonal.Application.Review;

import com.marketplace.hexagonal.Application.Review.UseCases.AddReviewUseCaseImpl;
import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Review.Exceptions.CannotReviewOwnContentException;
import com.marketplace.hexagonal.Domain.Review.Exceptions.InvalidRatingException;
import com.marketplace.hexagonal.Domain.Review.Exceptions.ReviewAlreadyExistsException;
import com.marketplace.hexagonal.Domain.Review.Models.Review;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewTargetType;
import com.marketplace.hexagonal.Domain.Review.Ports.Out.ReviewRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerStatus;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddReviewUseCaseImplTest {

    @Mock ReviewRepositoryPort reviewRepo;
    @Mock ProductRepositoryPort productRepo;
    @Mock SellerProfileRepositoryPort sellerProfileRepo;
    AddReviewUseCaseImpl useCase;

    private static final UserId AUTHOR_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final UserId SELLER_UID = UserId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final ProductId PRODUCT_ID = ProductId.of("aa000000-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() {
        useCase = new AddReviewUseCaseImpl(reviewRepo, productRepo, sellerProfileRepo);
    }

    @Test
    void addProductReview_valid_savesReview() {
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(buildProduct()));
        when(reviewRepo.existsByAuthorIdAndTargetTypeAndTargetId(any(), any(), any())).thenReturn(false);
        when(reviewRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Review result = useCase.addReview(
                AUTHOR_ID, "Juan", ReviewTargetType.PRODUCT, PRODUCT_ID.getValue(), 4, "Great!");

        assertThat(result.getRating()).isEqualTo(4);
        assertThat(result.getComment()).isEqualTo("Great!");
        assertThat(result.getAuthorName()).isEqualTo("Juan");
        assertThat(result.getTargetType()).isEqualTo(ReviewTargetType.PRODUCT);
        verify(reviewRepo).save(any());
    }

    @Test
    void addSellerReview_valid_savesReview() {
        when(sellerProfileRepo.findByUserId(SELLER_UID)).thenReturn(Optional.of(buildSellerProfile()));
        when(reviewRepo.existsByAuthorIdAndTargetTypeAndTargetId(any(), any(), any())).thenReturn(false);
        when(reviewRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Review result = useCase.addReview(
                AUTHOR_ID, "Juan", ReviewTargetType.SELLER, SELLER_UID.getValue(), 5, "Excellent seller!");

        assertThat(result.getTargetType()).isEqualTo(ReviewTargetType.SELLER);
        assertThat(result.getRating()).isEqualTo(5);
        verify(reviewRepo).save(any());
    }

    @Test
    void addReview_invalidRating_throwsInvalidRatingException() {
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(buildProduct()));
        when(reviewRepo.existsByAuthorIdAndTargetTypeAndTargetId(any(), any(), any())).thenReturn(false);

        assertThatThrownBy(() -> useCase.addReview(
                AUTHOR_ID, "Juan", ReviewTargetType.PRODUCT, PRODUCT_ID.getValue(), 6, "Bad!"))
                .isInstanceOf(InvalidRatingException.class);

        verify(reviewRepo, never()).save(any());
    }

    @Test
    void addProductReview_productNotFound_throwsProductNotFoundException() {
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.addReview(
                AUTHOR_ID, "Juan", ReviewTargetType.PRODUCT, PRODUCT_ID.getValue(), 3, "OK"))
                .isInstanceOf(ProductNotFoundException.class);

        verify(reviewRepo, never()).save(any());
    }

    @Test
    void addProductReview_duplicate_throwsReviewAlreadyExistsException() {
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(buildProduct()));
        when(reviewRepo.existsByAuthorIdAndTargetTypeAndTargetId(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> useCase.addReview(
                AUTHOR_ID, "Juan", ReviewTargetType.PRODUCT, PRODUCT_ID.getValue(), 5, "Again!"))
                .isInstanceOf(ReviewAlreadyExistsException.class);

        verify(reviewRepo, never()).save(any());
    }

    @Test
    void addSellerReview_selfReview_throwsCannotReviewOwnContentException() {
        when(sellerProfileRepo.findByUserId(SELLER_UID)).thenReturn(Optional.of(buildSellerProfile()));

        assertThatThrownBy(() -> useCase.addReview(
                SELLER_UID, "Seller", ReviewTargetType.SELLER, SELLER_UID.getValue(), 5, "I love myself!"))
                .isInstanceOf(CannotReviewOwnContentException.class);

        verify(reviewRepo, never()).save(any());
    }

    @Test
    void calculateAverageRating_multipleReviews_returnsCorrectAverage() {
        List<Review> reviews = List.of(
                buildReview(5), buildReview(4), buildReview(3));

        BigDecimal avg = Review.calculateAverageRating(reviews);

        assertThat(avg).isEqualByComparingTo("4.00");
    }

    @Test
    void calculateAverageRating_emptyList_returnsZero() {
        assertThat(Review.calculateAverageRating(List.of()))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    private Product buildProduct() {
        return Product.builder()
                .id(PRODUCT_ID)
                .name("Test Product")
                .slug("test-product")
                .basePrice(Money.of(new BigDecimal("99.99"), "USD"))
                .images(List.of())
                .build();
    }

    private SellerProfile buildSellerProfile() {
        return SellerProfile.builder()
                .id(SellerProfileId.of("sp-001"))
                .userId(SELLER_UID)
                .storeName("Test Store")
                .status(SellerStatus.ACTIVE)
                .build();
    }

    private Review buildReview(int rating) {
        return Review.builder()
                .reviewId(com.marketplace.hexagonal.Domain.Review.Models.ReviewId.generate())
                .authorId(AUTHOR_ID)
                .authorName("Juan")
                .targetType(ReviewTargetType.PRODUCT)
                .targetId(PRODUCT_ID.getValue())
                .rating(rating)
                .comment("Comment")
                .createdAt(java.time.Instant.now())
                .updatedAt(java.time.Instant.now())
                .build();
    }
}
