package com.marketplace.hexagonal.Domain.Cart.Exceptions;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(String message) { super(message); }
}
