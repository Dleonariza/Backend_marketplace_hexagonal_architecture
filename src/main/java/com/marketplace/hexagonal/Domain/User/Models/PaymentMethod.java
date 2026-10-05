package com.marketplace.hexagonal.Domain.User.Models;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentMethod {

    private PaymentMethodId id;
    private UserId userId;

    private String stripePaymentMethodId;

    private String last4;

    private String brand;

    private int expMonth;
    private int expYear;

    private String cardholderName;
    private boolean isDefault;
    private Instant createdAt;

    public static PaymentMethod create(UserId userId,
                                       String stripePaymentMethodId,
                                       String last4,
                                       String brand,
                                       int expMonth,
                                       int expYear,
                                       String cardholderName,
                                       boolean isDefault) {
        return PaymentMethod.builder()
                .id(PaymentMethodId.generate())
                .userId(userId)
                .stripePaymentMethodId(stripePaymentMethodId)
                .last4(last4)
                .brand(brand != null ? brand.toLowerCase() : null)
                .expMonth(expMonth)
                .expYear(expYear)
                .cardholderName(cardholderName)
                .isDefault(isDefault)
                .createdAt(Instant.now())
                .build();
    }
}
