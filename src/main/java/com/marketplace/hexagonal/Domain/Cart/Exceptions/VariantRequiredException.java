package com.marketplace.hexagonal.Domain.Cart.Exceptions;

public class VariantRequiredException extends RuntimeException {
    public VariantRequiredException(String productName) {
        super("Product \"" + productName + "\" has multiple variants. Please select a specific variant.");
    }
}
