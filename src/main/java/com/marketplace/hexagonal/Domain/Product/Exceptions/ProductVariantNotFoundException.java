package com.marketplace.hexagonal.Domain.Product.Exceptions;

import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;

public class ProductVariantNotFoundException extends RuntimeException {
    public ProductVariantNotFoundException(ProductVariantId id) {
        super("Product variant not found with id: " + id.getValue());
    }
}
