package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomType;
import com.marketplace.hexagonal.Infrastructura.Entities.Chat.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaChatRoomRepository extends JpaRepository<ChatRoomEntity, UUID> {

    @Query("""
           SELECT r FROM ChatRoomEntity r
           WHERE r.orderId = :orderId
             AND r.type    = :type
             AND (:sellerUserId IS NULL AND r.sellerUser IS NULL
                  OR r.sellerUser.id = :sellerUserId)
           """)
    Optional<ChatRoomEntity> findByOrderIdAndTypeAndSellerUserId(
            @Param("orderId") UUID orderId,
            @Param("type") ChatRoomType type,
            @Param("sellerUserId") UUID sellerUserId);
    List<ChatRoomEntity> findByBuyer_Id(UUID buyerId);

    List<ChatRoomEntity> findBySellerUser_Id(UUID sellerUserId);

    List<ChatRoomEntity> findByType(ChatRoomType type);

    @Modifying
    @Query("UPDATE ChatRoomEntity r SET r.lastActivityAt = :ts WHERE r.id = :id")
    void updateLastActivity(@Param("id") UUID id, @Param("ts") Instant ts);

    @Modifying
    @Query("DELETE FROM ChatRoomEntity r WHERE r.type = 'BUYER_SUPPORT' AND r.lastActivityAt < :cutoff")
    int deleteByTypeAndLastActivityAtBefore(@Param("cutoff") Instant cutoff);
}
