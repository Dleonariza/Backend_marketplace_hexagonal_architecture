package com.marketplace.hexagonal.Domain.Product.Ports.In;

import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;

public interface DeactivateCategoryUseCase {
    Category deactivateCategory(CategoryId id);
}
