package com.marketplace.hexagonal.Domain.Product.Exceptions;

import com.marketplace.hexagonal.Domain.Product.Models.ProductId;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(ProductId id) {
        super("Product not found with id: " + id.getValue());
    }
}
