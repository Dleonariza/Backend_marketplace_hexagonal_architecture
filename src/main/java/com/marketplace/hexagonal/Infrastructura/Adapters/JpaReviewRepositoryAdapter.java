package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Review.Models.Review;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewId;
import com.marketplace.hexagonal.Domain.Review.Models.ReviewTargetType;
import com.marketplace.hexagonal.Domain.Review.Ports.Out.ReviewRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Entities.Review.ReviewEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaReviewRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaReviewRepositoryAdapter implements ReviewRepositoryPort {

    private final JpaReviewRepository jpaReviewRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    @Transactional
    public Review save(Review review) {
        UserEntity author = jpaUserRepository
                .findById(UUID.fromString(review.getAuthorId().getValue()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Author not found: " + review.getAuthorId().getValue()));

        ReviewEntity entity = ReviewEntity.builder()
                .author(author)
                .authorName(review.getAuthorName())
                .targetType(review.getTargetType())
                .targetId(UUID.fromString(review.getTargetId()))
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();

        return toDomain(jpaReviewRepository.save(entity));
    }

    @Override
    public Optional<Review> findById(ReviewId reviewId) {
        return jpaReviewRepository
                .findById(UUID.fromString(reviewId.getValue()))
                .map(this::toDomain);
    }

    @Override
    public List<Review> findAllByTargetTypeAndTargetId(ReviewTargetType targetType, String targetId) {
        return jpaReviewRepository
                .findAllByTargetTypeAndTargetIdOrderByCreatedAtDesc(
                        targetType, UUID.fromString(targetId))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByAuthorIdAndTargetTypeAndTargetId(UserId authorId,
                                                            ReviewTargetType targetType,
                                                            String targetId) {
        return jpaReviewRepository.existsByAuthor_IdAndTargetTypeAndTargetId(
                UUID.fromString(authorId.getValue()),
                targetType,
                UUID.fromString(targetId));
    }

    @Override
    @Transactional
    public void deleteById(ReviewId reviewId) {
        jpaReviewRepository.deleteReviewById(UUID.fromString(reviewId.getValue()));
    }

    private Review toDomain(ReviewEntity e) {
        return Review.builder()
                .reviewId(ReviewId.of(e.getId().toString()))
                .authorId(UserId.of(e.getAuthor().getId().toString()))
                .authorName(e.getAuthorName())
                .targetType(e.getTargetType())
                .targetId(e.getTargetId().toString())
                .rating(e.getRating())
                .comment(e.getComment())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
