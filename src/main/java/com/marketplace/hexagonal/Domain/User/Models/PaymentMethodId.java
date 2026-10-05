package com.marketplace.hexagonal.Domain.User.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentMethodId {
    private final String value;

    public PaymentMethodId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("PaymentMethodId can't be null or blank");
        this.value = value;
    }

    public static PaymentMethodId generate() {
        return new PaymentMethodId(UUID.randomUUID().toString());
    }

    public static PaymentMethodId of(String value) {
        return new PaymentMethodId(value);
    }
}
