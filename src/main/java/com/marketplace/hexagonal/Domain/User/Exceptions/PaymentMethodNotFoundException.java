package com.marketplace.hexagonal.Domain.User.Exceptions;

import com.marketplace.hexagonal.Domain.User.Models.PaymentMethodId;

public class PaymentMethodNotFoundException extends RuntimeException {

    public PaymentMethodNotFoundException(PaymentMethodId id) {
        super("Payment method not found with ID: " + id.getValue());
    }

    public PaymentMethodNotFoundException(String message) {
        super(message);
    }
}
