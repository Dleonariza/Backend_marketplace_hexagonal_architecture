package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.CategoryRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.CategoryEntity;
import com.marketplace.hexagonal.Infrastructura.Mappers.CategoryMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JpaCategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final JpaCategoryRepository jpaCategoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Category save(Category category) {
        CategoryEntity parentEntity = resolveParent(category.getParentId());
        CategoryEntity entity = categoryMapper.toEntity(category, parentEntity);
        CategoryEntity saved = jpaCategoryRepository.save(entity);
        return categoryMapper.toDomain(saved);
    }

    @Override
    public Optional<Category> findById(CategoryId id) {
        return jpaCategoryRepository.findById(UUID.fromString(id.getValue()))
                .map(categoryMapper::toDomain);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return jpaCategoryRepository.findBySlug(slug)
                .map(categoryMapper::toDomain);
    }

    @Override
    public List<Category> findByParentId(CategoryId parentId) {
        return jpaCategoryRepository.findByParent_Id(UUID.fromString(parentId.getValue()))
                .stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> findAllActive() {
        return jpaCategoryRepository.findByActiveTrue().stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> findAll() {
        return jpaCategoryRepository.findAll().stream()
                .map(categoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaCategoryRepository.existsBySlug(slug);
    }

    private CategoryEntity resolveParent(CategoryId parentId) {
        if (parentId == null) return null;
        return jpaCategoryRepository.findById(UUID.fromString(parentId.getValue()))
                .orElse(null);
    }
}
