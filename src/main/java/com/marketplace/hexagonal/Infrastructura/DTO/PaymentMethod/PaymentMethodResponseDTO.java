package com.marketplace.hexagonal.Infrastructura.DTO.PaymentMethod;

import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;

import java.time.Instant;

public record PaymentMethodResponseDTO(
        String id,
        String last4,
        String brand,
        int expMonth,
        int expYear,
        String cardholderName,
        boolean isDefault,
        Instant createdAt
) {
    public static PaymentMethodResponseDTO from(PaymentMethod pm) {
        return new PaymentMethodResponseDTO(
                pm.getId().getValue(),
                pm.getLast4(),
                pm.getBrand(),
                pm.getExpMonth(),
                pm.getExpYear(),
                pm.getCardholderName(),
                pm.isDefault(),
                pm.getCreatedAt()
        );
    }
}
