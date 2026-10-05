package com.marketplace.hexagonal.Domain.Cart.Exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) { super(message); }
}
