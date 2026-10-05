package com.marketplace.hexagonal.Domain.Product.Exceptions;

public class DuplicateSkuException extends RuntimeException {
    public DuplicateSkuException(String sku) {
        super("A variant with SKU '" + sku + "' already exists in this product.");
    }
}
