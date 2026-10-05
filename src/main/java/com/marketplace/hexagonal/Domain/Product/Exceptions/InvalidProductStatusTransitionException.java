package com.marketplace.hexagonal.Domain.Product.Exceptions;

import com.marketplace.hexagonal.Domain.Product.Models.ProductStatus;

public class InvalidProductStatusTransitionException extends RuntimeException {
    public InvalidProductStatusTransitionException(ProductStatus from, ProductStatus to) {
        super("Cannot transition product status from " + from + " to " + to + ".");
    }
    public InvalidProductStatusTransitionException(String message) {
        super(message);
    }
}
