package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Exceptions.CategoryNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.DeactivateCategoryUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeactivateCategoryUseCaseImpl implements DeactivateCategoryUseCase {

    private final CategoryRepositoryPort categoryRepo;

    @Override
    public Category deactivateCategory(CategoryId id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        category.deactivate();
        return categoryRepo.save(category);
    }
}
