package com.marketplace.hexagonal.Infrastructura.DTO.Order;

import com.marketplace.hexagonal.Domain.Order.Models.PaymentInfo;

public record PaymentInfoResponseDTO(
        String paymentMethodId,
        String last4,
        String brand,
        int expMonth,
        int expYear,
        String cardholderName
) {
    public static PaymentInfoResponseDTO from(PaymentInfo info) {
        if (info == null) return null;
        return new PaymentInfoResponseDTO(
                info.paymentMethodId(),
                info.last4(),
                info.brand(),
                info.expMonth(),
                info.expYear(),
                info.cardholderName()
        );
    }
}
