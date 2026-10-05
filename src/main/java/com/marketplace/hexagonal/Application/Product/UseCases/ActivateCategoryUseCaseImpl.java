package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Exceptions.CategoryNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.ActivateCategoryUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActivateCategoryUseCaseImpl implements ActivateCategoryUseCase {

    private final CategoryRepositoryPort categoryRepo;

    @Override
    public Category activateCategory(CategoryId id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        category.activate();
        return categoryRepo.save(category);
    }
}
