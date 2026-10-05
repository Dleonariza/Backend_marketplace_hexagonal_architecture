package com.marketplace.hexagonal.Domain.Product.Exceptions;

public class ProductOwnershipException extends RuntimeException {
    public ProductOwnershipException() {
        super("You do not have permission to modify this product.");
    }
}
