package com.marketplace.hexagonal.Application.Product.Services;

import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.*;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CategoryService implements
        CreateCategoryUseCase,
        UpdateCategoryUseCase,
        GetCategoryUseCase,
        ListCategoriesUseCase,
        ActivateCategoryUseCase,
        DeactivateCategoryUseCase {
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final ActivateCategoryUseCase activateCategoryUseCase;
    private final DeactivateCategoryUseCase deactivateCategoryUseCase;

    @Override
    public Category createCategory(String name, String description, String slug, CategoryId parentId) {
        return createCategoryUseCase.createCategory(name, description, slug, parentId);
    }

    @Override
    public Category updateCategory(CategoryId id, String name, String description, String slug) {
        return updateCategoryUseCase.updateCategory(id, name, description, slug);
    }

    @Override
    public Category getCategoryById(CategoryId id) {
        return getCategoryUseCase.getCategoryById(id);
    }

    @Override
    public Category getCategoryBySlug(String slug) {
        return getCategoryUseCase.getCategoryBySlug(slug);
    }

    @Override
    public List<Category> getSubcategories(CategoryId parentId) {
        return getCategoryUseCase.getSubcategories(parentId);
    }

    @Override
    public List<Category> getAllCategories() {
        return listCategoriesUseCase.getAllCategories();
    }

    @Override
    public List<Category> getActiveCategories() {
        return listCategoriesUseCase.getActiveCategories();
    }

    @Override
    public Category activateCategory(CategoryId id) {
        return activateCategoryUseCase.activateCategory(id);
    }

    @Override
    public Category deactivateCategory(CategoryId id) {
        return deactivateCategoryUseCase.deactivateCategory(id);
    }
}
