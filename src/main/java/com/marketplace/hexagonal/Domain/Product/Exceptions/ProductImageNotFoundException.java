package com.marketplace.hexagonal.Domain.Product.Exceptions;

import com.marketplace.hexagonal.Domain.Product.Models.ProductImageId;

public class ProductImageNotFoundException extends RuntimeException {
    public ProductImageNotFoundException(ProductImageId id) {
        super("Product image not found with id: " + id.getValue());
    }
}
