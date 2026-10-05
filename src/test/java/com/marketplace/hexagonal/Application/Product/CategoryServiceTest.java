package com.marketplace.hexagonal.Application.Product;

import com.marketplace.hexagonal.Application.Product.Services.CategoryService;
import com.marketplace.hexagonal.Application.Product.UseCases.*;
import com.marketplace.hexagonal.Domain.Product.Exceptions.CategoryNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.SlugAlreadyExistsException;
import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.CategoryRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock CategoryRepositoryPort categoryRepo;
    CategoryService categoryService;

    private static final CategoryId CATEGORY_ID = CategoryId.of("550e8400-e29b-41d4-a716-446655440000");

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(
                new CreateCategoryUseCaseImpl(categoryRepo),
                new UpdateCategoryUseCaseImpl(categoryRepo),
                new GetCategoryUseCaseImpl(categoryRepo),
                new ListCategoriesUseCaseImpl(categoryRepo),
                new ActivateCategoryUseCaseImpl(categoryRepo),
                new DeactivateCategoryUseCaseImpl(categoryRepo)
        );
    }

    @Test
    void createCategory_whenSlugAvailable_shouldSaveAndReturn() {
        when(categoryRepo.existsBySlug("electronics")).thenReturn(false);
        when(categoryRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Category result = categoryService.createCategory("Electronics", "Devices", "electronics", null);

        assertThat(result.getName()).isEqualTo("Electronics");
        assertThat(result.getSlug()).isEqualTo("electronics");
        assertThat(result.isActive()).isTrue();
        verify(categoryRepo).save(any());
    }

    @Test
    void createCategory_whenSlugTaken_shouldThrow() {
        when(categoryRepo.existsBySlug("electronics")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory("Electronics", "Devices", "electronics", null))
                .isInstanceOf(SlugAlreadyExistsException.class);
    }

    @Test
    void createCategory_withParent_shouldPassParentId() {
        CategoryId parentId = CategoryId.of("660e8400-e29b-41d4-a716-446655440001");
        when(categoryRepo.existsBySlug("smartphones")).thenReturn(false);
        when(categoryRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Category result = categoryService.createCategory("Smartphones", "Mobile phones", "smartphones", parentId);

        assertThat(result.getParentId()).isEqualTo(parentId);
    }

    @Test
    void updateCategory_whenFound_shouldUpdateAndSave() {
        Category existing = buildCategory("Old Name", "old-slug");
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(existing));
        when(categoryRepo.existsBySlug("new-slug")).thenReturn(false);
        when(categoryRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Category result = categoryService.updateCategory(CATEGORY_ID, "New Name", "New desc", "new-slug");

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getSlug()).isEqualTo("new-slug");
        verify(categoryRepo).save(any());
    }

    @Test
    void updateCategory_whenSameSlug_shouldAllowUpdate() {
        Category existing = buildCategory("Old Name", "same-slug");
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(existing));
        when(categoryRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Category result = categoryService.updateCategory(CATEGORY_ID, "Updated Name", "desc", "same-slug");

        assertThat(result.getName()).isEqualTo("Updated Name");
        verify(categoryRepo, never()).existsBySlug(any());
    }

    @Test
    void updateCategory_whenNotFound_shouldThrow() {
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(CATEGORY_ID, "name", "desc", "slug"))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void updateCategory_whenSlugTakenByOther_shouldThrow() {
        Category existing = buildCategory("Name", "original-slug");
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(existing));
        when(categoryRepo.existsBySlug("taken-slug")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.updateCategory(CATEGORY_ID, "name", "desc", "taken-slug"))
                .isInstanceOf(SlugAlreadyExistsException.class);
    }

    @Test
    void getCategoryById_whenFound_shouldReturn() {
        Category category = buildCategory("Electronics", "electronics");
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(CATEGORY_ID);

        assertThat(result.getName()).isEqualTo("Electronics");
    }

    @Test
    void getCategoryById_whenNotFound_shouldThrow() {
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(CATEGORY_ID))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void getCategoryBySlug_whenFound_shouldReturn() {
        Category category = buildCategory("Electronics", "electronics");
        when(categoryRepo.findBySlug("electronics")).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryBySlug("electronics");

        assertThat(result.getSlug()).isEqualTo("electronics");
    }

    @Test
    void getCategoryBySlug_whenNotFound_shouldThrow() {
        when(categoryRepo.findBySlug("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryBySlug("nonexistent"))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void getAllCategories_shouldReturnAll() {
        when(categoryRepo.findAll()).thenReturn(List.of(
                buildCategory("A", "a"), buildCategory("B", "b")));

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
    }

    @Test
    void getActiveCategories_shouldReturnOnlyActive() {
        Category active = buildCategory("Active", "active");
        when(categoryRepo.findAllActive()).thenReturn(List.of(active));

        List<Category> result = categoryService.getActiveCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActive()).isTrue();
    }

    @Test
    void activateCategory_whenFound_shouldSetActiveAndSave() {
        Category category = buildCategory("Name", "slug");
        category.deactivate();
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Category result = categoryService.activateCategory(CATEGORY_ID);

        assertThat(result.isActive()).isTrue();
        verify(categoryRepo).save(any());
    }

    @Test
    void activateCategory_whenNotFound_shouldThrow() {
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.activateCategory(CATEGORY_ID))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void deactivateCategory_whenFound_shouldSetInactiveAndSave() {
        Category category = buildCategory("Name", "slug");
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Category result = categoryService.deactivateCategory(CATEGORY_ID);

        assertThat(result.isActive()).isFalse();
        verify(categoryRepo).save(any());
    }

    @Test
    void deactivateCategory_whenNotFound_shouldThrow() {
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deactivateCategory(CATEGORY_ID))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void getSubcategories_shouldReturnChildrenOfParent() {
        CategoryId parentId = CategoryId.of("660e8400-e29b-41d4-a716-446655440001");
        Category child = buildCategory("Smartphones", "smartphones");
        when(categoryRepo.findByParentId(parentId)).thenReturn(List.of(child));

        List<Category> result = categoryService.getSubcategories(parentId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Smartphones");
    }

    private Category buildCategory(String name, String slug) {
        return Category.create(name, "Description for " + name, slug, null);
    }
}
