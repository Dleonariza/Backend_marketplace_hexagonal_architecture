package com.marketplace.hexagonal.Domain.Shipping.Exceptions;

public class ShippingOptionOwnershipException extends RuntimeException {
    public ShippingOptionOwnershipException() {
        super("You do not own this shipping option.");
    }
}
