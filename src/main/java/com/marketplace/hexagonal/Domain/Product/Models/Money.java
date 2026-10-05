package com.marketplace.hexagonal.Domain.Product.Models;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {

    public Money {
        Objects.requireNonNull(amount, "Money amount cannot be null");
        Objects.requireNonNull(currency, "Money currency cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Money amount cannot be negative");
        if (currency.isBlank() || currency.length() != 3)
            throw new IllegalArgumentException("Currency must be a 3-letter ISO-4217 code");
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }
}
