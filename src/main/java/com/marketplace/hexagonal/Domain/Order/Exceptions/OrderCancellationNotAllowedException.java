package com.marketplace.hexagonal.Domain.Order.Exceptions;

public class OrderCancellationNotAllowedException extends RuntimeException {
    public OrderCancellationNotAllowedException(String currentStatus) {
        super("Order cannot be cancelled in its current status: " + currentStatus + ".");
    }
}
