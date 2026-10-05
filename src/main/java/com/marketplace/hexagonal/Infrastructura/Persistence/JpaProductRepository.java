package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Domain.Product.Models.ProductStatus;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID>,
        JpaSpecificationExecutor<ProductEntity> {
    List<ProductEntity> findBySeller_Id(UUID sellerId);
    List<ProductEntity> findBySeller_IdAndStatus(UUID sellerId, ProductStatus status);
    List<ProductEntity> findByStatus(ProductStatus status);
    boolean existsBySlug(String slug);
}
