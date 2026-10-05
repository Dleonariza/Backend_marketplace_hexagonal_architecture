package com.marketplace.hexagonal.Domain.User.Exceptions;

import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;

public class AddressNotFoundException extends RuntimeException {

    public AddressNotFoundException(UserAddressId id) {
        super("Address not found with ID: " + id.getValue());
    }

    public AddressNotFoundException(String message) {
        super(message);
    }
}
