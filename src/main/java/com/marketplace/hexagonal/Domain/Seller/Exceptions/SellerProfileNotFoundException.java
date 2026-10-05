package com.marketplace.hexagonal.Domain.Seller.Exceptions;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public class SellerProfileNotFoundException extends RuntimeException {

    public SellerProfileNotFoundException(SellerProfileId id) {
        super("Seller profile not found with id: " + id.getValue());
    }

    public SellerProfileNotFoundException(String message) {
        super(message);
    }
}
