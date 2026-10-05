package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Exceptions.CategoryNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.GetCategoryUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetCategoryUseCaseImpl implements GetCategoryUseCase {

    private final CategoryRepositoryPort categoryRepo;

    @Override
    public Category getCategoryById(CategoryId id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    public Category getCategoryBySlug(String slug) {
        return categoryRepo.findBySlug(slug)
                .orElseThrow(() -> new CategoryNotFoundException(slug));
    }

    @Override
    public List<Category> getSubcategories(CategoryId parentId) {
        return categoryRepo.findByParentId(parentId);
    }
}
