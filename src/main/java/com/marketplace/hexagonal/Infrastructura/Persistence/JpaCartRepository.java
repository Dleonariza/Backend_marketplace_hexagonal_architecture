package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Domain.Cart.Models.CartStatus;
import com.marketplace.hexagonal.Infrastructura.Entities.Cart.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaCartRepository extends JpaRepository<CartEntity, UUID> {

    Optional<CartEntity> findByUser_IdAndStatusAndExpiresAtAfter(
            UUID userId, CartStatus status, Instant now);

    Optional<CartEntity> findBySessionIdAndStatusAndExpiresAtAfter(
            String sessionId, CartStatus status, Instant now);

    @Query("SELECT c FROM CartEntity c WHERE c.status = 'ACTIVE' AND c.expiresAt < :now")
    List<CartEntity> findExpiredActiveCarts(@Param("now") Instant now);
}
