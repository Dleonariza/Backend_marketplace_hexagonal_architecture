package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Ports.In.ListCategoriesUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListCategoriesUseCaseImpl implements ListCategoriesUseCase {

    private final CategoryRepositoryPort categoryRepo;

    @Override public List<Category> getAllCategories() { return categoryRepo.findAll(); }
    @Override public List<Category> getActiveCategories() { return categoryRepo.findAllActive(); }
}
