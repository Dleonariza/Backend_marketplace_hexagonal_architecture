package com.marketplace.hexagonal.Domain.Cart.Exceptions;

public class CartAlreadyCheckedOutException extends RuntimeException {
    public CartAlreadyCheckedOutException(String message) { super(message); }
}
