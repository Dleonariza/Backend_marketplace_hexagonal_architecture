package com.marketplace.hexagonal.Domain.Order.Exceptions;

public class SubOrderAccessDeniedException extends RuntimeException {
    public SubOrderAccessDeniedException() {
        super("You do not have access to this sub-order.");
    }
}
