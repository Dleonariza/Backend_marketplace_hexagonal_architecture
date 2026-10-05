package com.marketplace.hexagonal.Domain.Product.Models;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Category {

    private CategoryId id;
    private String name;
    private String description;

    private String slug;

    private CategoryId parentId;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public static Category create(
            String name,
            String description,
            String slug,
            CategoryId parentId) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Category name cannot be blank");
        if (slug == null || slug.isBlank())
            throw new IllegalArgumentException("Category slug cannot be blank");

        Instant now = Instant.now();
        return Category.builder()
                .id(CategoryId.generate())
                .name(name.trim())
                .description(description)
                .slug(slug.trim().toLowerCase())
                .parentId(parentId)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static Category reconstitute(
            CategoryId id,
            String name,
            String description,
            String slug,
            CategoryId parentId,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {
        Category c = new Category();
        c.id = id;
        c.name = name;
        c.description = description;
        c.slug = slug;
        c.parentId = parentId;
        c.active = active;
        c.createdAt = createdAt;
        c.updatedAt = updatedAt;
        return c;
    }

    public void update(String name, String description, String slug) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Category name cannot be blank");
        if (slug == null || slug.isBlank())
            throw new IllegalArgumentException("Category slug cannot be blank");

        this.name = name.trim();
        this.description = description;
        this.slug = slug.trim().toLowerCase();
        this.updatedAt = Instant.now();
    }

    public void activate() { this.active = true; this.updatedAt = Instant.now(); }
    public void deactivate() { this.active = false; this.updatedAt = Instant.now(); }
}
