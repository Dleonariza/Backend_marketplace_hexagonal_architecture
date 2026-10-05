package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Domain.Order.Models.OrderStatus;
import com.marketplace.hexagonal.Infrastructura.Entities.Order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaOrderRepository
        extends JpaRepository<OrderEntity, UUID>,
                JpaSpecificationExecutor<OrderEntity> {
    Optional<OrderEntity> findByUser_IdAndIdempotencyKey(UUID userId, String idempotencyKey);

    List<OrderEntity> findByUser_IdOrderByCreatedAtDesc(UUID userId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM OrderEntity o " +
           "WHERE o.status IN ('PAID','PROCESSING','SHIPPED','DELIVERED')")
    BigDecimal sumGmv();

    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.createdAt >= :since")
    long countOrdersSince(Instant since);

    @Query("SELECT o.status, COUNT(o) FROM OrderEntity o GROUP BY o.status")
    List<Object[]> countByStatus();

    @Query("SELECT COUNT(o) FROM OrderEntity o")
    long countAll();

    boolean existsByNumOrder(String numOrder);
}
