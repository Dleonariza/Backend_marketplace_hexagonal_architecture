package com.marketplace.hexagonal.Domain.Order.Exceptions;

public class SubOrderNotFoundException extends RuntimeException {
    public SubOrderNotFoundException(String id) {
        super("Sub-order not found: " + id);
    }
}
