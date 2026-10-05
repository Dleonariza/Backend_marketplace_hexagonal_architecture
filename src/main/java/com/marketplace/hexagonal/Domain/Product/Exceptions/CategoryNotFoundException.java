package com.marketplace.hexagonal.Domain.Product.Exceptions;

import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(CategoryId id) {
        super("Category not found with id: " + id.getValue());
    }
    public CategoryNotFoundException(String slug) {
        super("Category not found with slug: " + slug);
    }
}
