package com.marketplace.hexagonal.Infrastructura.DTO.Admin;

import com.marketplace.hexagonal.Domain.Admin.Models.CommissionConfig;

import java.math.BigDecimal;
import java.time.Instant;

public record CommissionConfigResponseDTO(
        Long id,
        BigDecimal rate,
        String description,
        Instant updatedAt
) {
    public static CommissionConfigResponseDTO from(CommissionConfig c) {
        return new CommissionConfigResponseDTO(
                c.id(), c.rate(), c.description(), c.updatedAt());
    }
}
