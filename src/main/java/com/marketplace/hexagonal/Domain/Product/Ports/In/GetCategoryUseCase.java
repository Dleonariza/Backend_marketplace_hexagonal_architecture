package com.marketplace.hexagonal.Domain.Product.Ports.In;

import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;

import java.util.List;

public interface GetCategoryUseCase {
    Category getCategoryById(CategoryId id);
    Category getCategoryBySlug(String slug);
    List<Category> getSubcategories(CategoryId parentId);
}
