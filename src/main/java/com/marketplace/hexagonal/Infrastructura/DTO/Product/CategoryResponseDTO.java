package com.marketplace.hexagonal.Infrastructura.DTO.Product;

import com.marketplace.hexagonal.Domain.Product.Models.Category;

import java.time.Instant;

public record CategoryResponseDTO(
        String id,
        String name,
        String description,
        String slug,
        String parentId,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static CategoryResponseDTO from(Category category) {
        return new CategoryResponseDTO(
                category.getId().getValue(),
                category.getName(),
                category.getDescription(),
                category.getSlug(),
                category.getParentId() != null ? category.getParentId().getValue() : null,
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
