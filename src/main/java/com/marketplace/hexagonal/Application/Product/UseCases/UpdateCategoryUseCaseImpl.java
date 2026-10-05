package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Exceptions.CategoryNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.SlugAlreadyExistsException;
import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.UpdateCategoryUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateCategoryUseCaseImpl implements UpdateCategoryUseCase {

    private final CategoryRepositoryPort categoryRepo;

    @Override
    public Category updateCategory(CategoryId id, String name, String description, String slug) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (!category.getSlug().equals(slug) && categoryRepo.existsBySlug(slug))
            throw new SlugAlreadyExistsException(slug);

        category.update(name, description, slug);
        return categoryRepo.save(category);
    }
}
