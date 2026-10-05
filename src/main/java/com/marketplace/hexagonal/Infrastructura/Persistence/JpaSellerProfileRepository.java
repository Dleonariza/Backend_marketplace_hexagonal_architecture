package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerStatus;
import com.marketplace.hexagonal.Infrastructura.Entities.Seller.SellerProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaSellerProfileRepository extends JpaRepository<SellerProfileEntity, UUID> {
    Optional<SellerProfileEntity> findByUser_Id(UUID userId);
    List<SellerProfileEntity> findByStatus(SellerStatus status);
}
