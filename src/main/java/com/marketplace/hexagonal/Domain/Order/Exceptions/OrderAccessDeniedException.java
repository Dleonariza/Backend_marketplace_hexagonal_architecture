package com.marketplace.hexagonal.Domain.Order.Exceptions;

public class OrderAccessDeniedException extends RuntimeException {
    public OrderAccessDeniedException() {
        super("You do not have access to this order.");
    }
}
