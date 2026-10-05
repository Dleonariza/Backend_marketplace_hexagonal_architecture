package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Infrastructura.Entities.Product.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    Optional<CategoryEntity> findBySlug(String slug);
    List<CategoryEntity> findByParent_Id(UUID parentId);
    List<CategoryEntity> findByActiveTrue();
    boolean existsBySlug(String slug);
}
