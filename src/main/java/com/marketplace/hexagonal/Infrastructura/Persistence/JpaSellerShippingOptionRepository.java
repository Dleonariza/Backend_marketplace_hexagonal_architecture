package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Infrastructura.Entities.Shipping.SellerShippingOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaSellerShippingOptionRepository
        extends JpaRepository<SellerShippingOptionEntity, UUID> {
    List<SellerShippingOptionEntity> findBySeller_Id(UUID sellerId);
    List<SellerShippingOptionEntity> findBySeller_IdAndActiveTrue(UUID sellerId);
}
