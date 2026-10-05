package com.marketplace.hexagonal.Application.Review;

import com.marketplace.hexagonal.Application.Review.UseCases.ListReviewsByTargetUseCaseImpl;
import com.marketplace.hexagonal.Domain.Review.Models.Review;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewId;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewTargetType;
import com.marketplace.hexagonal.Domain.Review.Ports.Out.ReviewRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListReviewsByTargetUseCaseImplTest {

    @Mock ReviewRepositoryPort reviewRepo;
    ListReviewsByTargetUseCaseImpl useCase;

    private static final String PRODUCT_ID = "aa000000-0000-0000-0000-000000000001";

    @BeforeEach
    void setUp() {
        useCase = new ListReviewsByTargetUseCaseImpl(reviewRepo);
    }

    @Test
    void listReviews_returnsAllReviewsForTarget() {
        List<Review> reviews = List.of(buildReview(5), buildReview(3));
        when(reviewRepo.findAllByTargetTypeAndTargetId(ReviewTargetType.PRODUCT, PRODUCT_ID))
                .thenReturn(reviews);

        List<Review> result = useCase.listReviews(ReviewTargetType.PRODUCT, PRODUCT_ID);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getRating()).isEqualTo(5);
        assertThat(result.get(1).getRating()).isEqualTo(3);
    }

    @Test
    void listReviews_noReviews_returnsEmptyList() {
        when(reviewRepo.findAllByTargetTypeAndTargetId(ReviewTargetType.PRODUCT, PRODUCT_ID))
                .thenReturn(List.of());

        assertThat(useCase.listReviews(ReviewTargetType.PRODUCT, PRODUCT_ID)).isEmpty();
    }

    private Review buildReview(int rating) {
        return Review.builder()
                .reviewId(ReviewId.generate())
                .authorId(UserId.of("550e8400-e29b-41d4-a716-446655440000"))
                .authorName("Juan")
                .targetType(ReviewTargetType.PRODUCT)
                .targetId(PRODUCT_ID)
                .rating(rating)
                .comment("Comment")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
