package com.marketplace.hexagonal.Domain.Product.Ports.Out;

import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    Category save(Category category);
    Optional<Category> findById(CategoryId id);
    Optional<Category> findBySlug(String slug);
    List<Category> findByParentId(CategoryId parentId);
    List<Category> findAllActive();
    List<Category> findAll();
    boolean existsBySlug(String slug);
}
