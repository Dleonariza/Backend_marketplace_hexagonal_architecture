package com.marketplace.hexagonal.Infrastructura.Mappers;

import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    default CategoryEntity toEntity(Category category, CategoryEntity parentEntity) {
        if (category == null) return null;

        return CategoryEntity.builder()
                .id(UUID.fromString(category.getId().getValue()))
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .parent(parentEntity)
                .active(category.isActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    default Category toDomain(CategoryEntity entity) {
        if (entity == null) return null;

        CategoryId parentId = entity.getParent() != null
                ? CategoryId.of(entity.getParent().getId().toString())
                : null;

        return Category.reconstitute(
                CategoryId.of(entity.getId().toString()),
                entity.getName(),
                entity.getDescription(),
                entity.getSlug(),
                parentId,
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
