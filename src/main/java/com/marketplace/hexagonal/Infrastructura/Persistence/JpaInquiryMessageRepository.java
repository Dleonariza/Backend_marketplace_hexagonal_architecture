package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Infrastructura.Entities.Inquiry.InquiryMessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JpaInquiryMessageRepository extends JpaRepository<InquiryMessageEntity, UUID> {

    List<InquiryMessageEntity> findByThread_IdOrderBySentAtDesc(UUID threadId, Pageable pageable);

    long countByThread_Id(UUID threadId);

    List<InquiryMessageEntity> findByThread_IdOrderBySentAtAsc(UUID threadId);

    @Modifying
    @Query("DELETE FROM InquiryMessageEntity m WHERE m.thread.id IN " +
           "(SELECT t.id FROM InquiryThreadEntity t WHERE t.lastActivityAt < :cutoff)")
    int deleteByThread_LastActivityAtBefore(@Param("cutoff") Instant cutoff);
}
