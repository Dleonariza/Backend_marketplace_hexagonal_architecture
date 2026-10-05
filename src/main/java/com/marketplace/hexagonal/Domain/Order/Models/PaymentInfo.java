package com.marketplace.hexagonal.Domain.Order.Models;

public record PaymentInfo(
        String paymentMethodId,
        String last4,
        String brand,
        int expMonth,
        int expYear,
        String cardholderName
) {}
