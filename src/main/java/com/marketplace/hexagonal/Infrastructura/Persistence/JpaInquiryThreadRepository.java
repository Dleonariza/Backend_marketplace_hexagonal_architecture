package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryStatus;
import com.marketplace.hexagonal.Infrastructura.Entities.Inquiry.InquiryThreadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaInquiryThreadRepository extends JpaRepository<InquiryThreadEntity, UUID> {

    Optional<InquiryThreadEntity> findByProductIdAndBuyer_Id(UUID productId, UUID buyerId);

    List<InquiryThreadEntity> findByBuyer_Id(UUID buyerId);

    List<InquiryThreadEntity> findBySellerUser_Id(UUID sellerUserId);

    @Modifying
    @Query("UPDATE InquiryThreadEntity t SET t.lastActivityAt = :ts WHERE t.id = :id")
    void updateLastActivity(@Param("id") UUID id, @Param("ts") Instant ts);

    @Modifying
    @Query("UPDATE InquiryThreadEntity t SET t.status = :status WHERE t.id = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") InquiryStatus status);

    @Modifying
    @Query("DELETE FROM InquiryThreadEntity t WHERE t.lastActivityAt < :cutoff")
    int deleteByLastActivityAtBefore(@Param("cutoff") Instant cutoff);
}
