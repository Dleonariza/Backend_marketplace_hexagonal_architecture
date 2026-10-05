package com.marketplace.hexagonal.Domain.User.Exceptions;

import com.marketplace.hexagonal.Domain.User.Models.UserId;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UserId userId) {
        super("User not found with ID: " + userId.getValue());
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
