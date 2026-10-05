package com.marketplace.hexagonal.Domain.Cart.Exceptions;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message) { super(message); }
}
