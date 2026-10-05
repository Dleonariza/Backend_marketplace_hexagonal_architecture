package com.marketplace.hexagonal.Domain.Auth.Exceptions;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(String message) {
        super(message);
    }
}
