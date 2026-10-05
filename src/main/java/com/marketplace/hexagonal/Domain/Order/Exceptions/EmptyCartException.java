package com.marketplace.hexagonal.Domain.Order.Exceptions;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException(String message) {
        super(message);
    }
}
