package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Infrastructura.Entities.Order.SubOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaSubOrderRepository extends JpaRepository<SubOrderEntity, UUID> {

    List<SubOrderEntity> findByOrder_IdOrderByCreatedAtDesc(UUID orderId);

    List<SubOrderEntity> findBySellerIdOrderByCreatedAtDesc(UUID sellerId);
}
