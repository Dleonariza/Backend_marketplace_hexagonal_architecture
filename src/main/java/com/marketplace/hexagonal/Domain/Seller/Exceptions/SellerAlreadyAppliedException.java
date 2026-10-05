package com.marketplace.hexagonal.Domain.Seller.Exceptions;

public class SellerAlreadyAppliedException extends RuntimeException {

    public SellerAlreadyAppliedException() {
        super("User already has an active, pending or suspended seller profile.");
    }
}
