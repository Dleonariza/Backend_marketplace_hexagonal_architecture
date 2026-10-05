package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Domain.Review.Models.ReviewTargetType;
import com.marketplace.hexagonal.Infrastructura.Entities.Review.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    List<ReviewEntity> findAllByTargetTypeAndTargetIdOrderByCreatedAtDesc(
            ReviewTargetType targetType, UUID targetId);

    boolean existsByAuthor_IdAndTargetTypeAndTargetId(
            UUID authorId, ReviewTargetType targetType, UUID targetId);

    @Modifying
    @Query("DELETE FROM ReviewEntity r WHERE r.id = :id")
    void deleteReviewById(UUID id);
}
