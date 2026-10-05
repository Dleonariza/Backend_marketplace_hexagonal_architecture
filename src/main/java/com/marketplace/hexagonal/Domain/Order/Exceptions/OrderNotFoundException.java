package com.marketplace.hexagonal.Domain.Order.Exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String id) {
        super("Order not found: " + id);
    }
}
