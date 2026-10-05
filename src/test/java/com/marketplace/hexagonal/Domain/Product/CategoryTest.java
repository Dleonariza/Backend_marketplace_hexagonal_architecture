package com.marketplace.hexagonal.Domain.Product;

import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CategoryTest {

    @Test
    void create_shouldGenerateIdAndBeActiveByDefault() {
        Category category = Category.create("Electronics", "Electronic devices", "electronics", null);

        assertThat(category.getId()).isNotNull();
        assertThat(category.getName()).isEqualTo("Electronics");
        assertThat(category.getSlug()).isEqualTo("electronics");
        assertThat(category.isActive()).isTrue();
        assertThat(category.getParentId()).isNull();
        assertThat(category.getCreatedAt()).isNotNull();
        assertThat(category.getUpdatedAt()).isNotNull();
    }

    @Test
    void create_withParent_shouldStoreParentId() {
        CategoryId parentId = CategoryId.of("550e8400-e29b-41d4-a716-446655440000");

        Category child = Category.create("Smartphones", "Mobile phones", "smartphones", parentId);

        assertThat(child.getParentId()).isEqualTo(parentId);
    }

    @Test
    void create_shouldNormalizeSlugToLowercase() {
        Category category = Category.create("My Category", null, "MY-SLUG", null);

        assertThat(category.getSlug()).isEqualTo("my-slug");
    }

    @Test
    void create_withBlankName_shouldThrow() {
        assertThatThrownBy(() -> Category.create("  ", "desc", "slug", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void create_withBlankSlug_shouldThrow() {
        assertThatThrownBy(() -> Category.create("Valid Name", "desc", "  ", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("slug");
    }

    @Test
    void create_withNullName_shouldThrow() {
        assertThatThrownBy(() -> Category.create(null, "desc", "slug", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void update_shouldChangeNameDescriptionAndSlug() {
        Category category = Category.create("Old Name", "Old desc", "old-slug", null);

        category.update("New Name", "New desc", "new-slug");

        assertThat(category.getName()).isEqualTo("New Name");
        assertThat(category.getDescription()).isEqualTo("New desc");
        assertThat(category.getSlug()).isEqualTo("new-slug");
        assertThat(category.getUpdatedAt()).isNotNull();
    }

    @Test
    void update_withBlankName_shouldThrow() {
        Category category = Category.create("Name", "desc", "slug", null);

        assertThatThrownBy(() -> category.update("", "desc", "slug"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void update_withBlankSlug_shouldThrow() {
        Category category = Category.create("Name", "desc", "slug", null);

        assertThatThrownBy(() -> category.update("Name", "desc", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deactivate_shouldSetActiveFalse() {
        Category category = Category.create("Name", "desc", "slug", null);

        category.deactivate();

        assertThat(category.isActive()).isFalse();
    }

    @Test
    void activate_shouldSetActiveTrue() {
        Category category = Category.create("Name", "desc", "slug", null);
        category.deactivate();

        category.activate();

        assertThat(category.isActive()).isTrue();
    }

    @Test
    void deactivateAndActivate_shouldUpdateTimestamp() {
        Category category = Category.create("Name", "desc", "slug", null);
        category.deactivate();
        assertThat(category.isActive()).isFalse();

        category.activate();
        assertThat(category.isActive()).isTrue();
    }
}
