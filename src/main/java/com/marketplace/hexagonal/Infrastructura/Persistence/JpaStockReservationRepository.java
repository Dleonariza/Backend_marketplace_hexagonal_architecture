package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Domain.Cart.Models.StockReservationStatus;
import com.marketplace.hexagonal.Infrastructura.Entities.Cart.StockReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaStockReservationRepository extends JpaRepository<StockReservationEntity, UUID> {

    @Query("""
        SELECT r FROM StockReservationEntity r
        WHERE r.cartId = :cartId
          AND r.product.id = :productId
          AND (:variantId IS NULL AND r.variant IS NULL
               OR r.variant.id = :variantId)
          AND r.status = 'RESERVED'
          AND r.expiresAt > :now
        """)
    Optional<StockReservationEntity> findActiveByCartAndProduct(
            @Param("cartId") UUID cartId,
            @Param("productId") UUID productId,
            @Param("variantId") UUID variantId,
            @Param("now") Instant now);
    @Query("""
        SELECT COALESCE(SUM(r.quantity), 0)
        FROM StockReservationEntity r
        WHERE r.product.id = :productId
          AND (:variantId IS NULL AND r.variant IS NULL
               OR r.variant.id = :variantId)
          AND r.status = 'RESERVED'
          AND r.expiresAt > :now
        """)
    int sumActiveReservedQuantity(
            @Param("productId") UUID productId,
            @Param("variantId") UUID variantId,
            @Param("now") Instant now);
    List<StockReservationEntity> findByCartIdAndStatus(UUID cartId, StockReservationStatus status);
}
