package com.marketplace.hexagonal.Domain.Admin.Models;

import java.math.BigDecimal;
import java.time.Instant;

public record CommissionConfig(
        Long id,
        BigDecimal rate,
        String description,
        Instant createdAt,
        Instant updatedAt
) {
    public static CommissionConfig create(BigDecimal rate, String description) {
        Instant now = Instant.now();
        return new CommissionConfig(null, rate, description, now, now);
    }
}
