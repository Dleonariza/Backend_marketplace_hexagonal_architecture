package com.marketplace.hexagonal.Domain.Seller.Exceptions;

public class SellerBannedException extends RuntimeException {

    public SellerBannedException() {
        super("This user is permanently banned from becoming a seller.");
    }
}
