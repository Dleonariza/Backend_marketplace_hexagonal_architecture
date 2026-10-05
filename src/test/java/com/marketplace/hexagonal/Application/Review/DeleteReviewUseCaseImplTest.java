package com.marketplace.hexagonal.Application.Review;

import com.marketplace.hexagonal.Application.Review.UseCases.DeleteReviewUseCaseImpl;
import com.marketplace.hexagonal.Domain.Review.Exceptions.ReviewAccessDeniedException;
import com.marketplace.hexagonal.Domain.Review.Exceptions.ReviewNotFoundException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteReviewUseCaseImplTest {

    @Mock ReviewRepositoryPort reviewRepo;
    DeleteReviewUseCaseImpl useCase;

    private static final UserId AUTHOR_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final UserId OTHER_ID = UserId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final ReviewId REVIEW_ID = ReviewId.generate();

    @BeforeEach
    void setUp() {
        useCase = new DeleteReviewUseCaseImpl(reviewRepo);
    }

    @Test
    void deleteReview_asAuthor_callsDeleteById() {
        when(reviewRepo.findById(REVIEW_ID)).thenReturn(Optional.of(buildReview(AUTHOR_ID)));

        useCase.deleteReview(REVIEW_ID, AUTHOR_ID);

        verify(reviewRepo).deleteById(REVIEW_ID);
    }

    @Test
    void deleteReview_notOwner_throwsReviewAccessDeniedException() {
        when(reviewRepo.findById(REVIEW_ID)).thenReturn(Optional.of(buildReview(AUTHOR_ID)));

        assertThatThrownBy(() -> useCase.deleteReview(REVIEW_ID, OTHER_ID))
                .isInstanceOf(ReviewAccessDeniedException.class);

        verify(reviewRepo, never()).deleteById(any());
    }

    @Test
    void deleteReview_notFound_throwsReviewNotFoundException() {
        when(reviewRepo.findById(REVIEW_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.deleteReview(REVIEW_ID, AUTHOR_ID))
                .isInstanceOf(ReviewNotFoundException.class);

        verify(reviewRepo, never()).deleteById(any());
    }

    private Review buildReview(UserId authorId) {
        return Review.builder()
                .reviewId(REVIEW_ID)
                .authorId(authorId)
                .authorName("Juan")
                .targetType(ReviewTargetType.PRODUCT)
                .targetId("aa000000-0000-0000-0000-000000000001")
                .rating(4)
                .comment("Great!")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
