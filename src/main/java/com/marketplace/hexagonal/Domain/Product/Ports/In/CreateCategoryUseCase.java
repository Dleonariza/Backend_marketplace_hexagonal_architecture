package com.marketplace.hexagonal.Domain.Product.Ports.In;

import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;

public interface CreateCategoryUseCase {
    Category createCategory(String name, String description, String slug, CategoryId parentId);
}
