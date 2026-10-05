package com.marketplace.hexagonal.Domain.Shipping.Exceptions;

public class ShippingOptionNotFoundException extends RuntimeException {
    public ShippingOptionNotFoundException(String id) {
        super("Shipping option not found: " + id);
    }
}
