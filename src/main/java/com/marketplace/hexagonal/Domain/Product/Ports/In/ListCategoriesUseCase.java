package com.marketplace.hexagonal.Domain.Product.Ports.In;

import com.marketplace.hexagonal.Domain.Product.Models.Category;

import java.util.List;

public interface ListCategoriesUseCase {
    List<Category> getAllCategories();
    List<Category> getActiveCategories();
}
