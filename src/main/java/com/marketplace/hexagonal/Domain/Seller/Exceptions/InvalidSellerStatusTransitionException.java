package com.marketplace.hexagonal.Domain.Seller.Exceptions;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerStatus;

public class InvalidSellerStatusTransitionException extends RuntimeException {

    public InvalidSellerStatusTransitionException(SellerStatus from, SellerStatus to) {
        super("Cannot transition seller status from " + from + " to " + to + ".");
    }

    public InvalidSellerStatusTransitionException(String message) {
        super(message);
    }
}
