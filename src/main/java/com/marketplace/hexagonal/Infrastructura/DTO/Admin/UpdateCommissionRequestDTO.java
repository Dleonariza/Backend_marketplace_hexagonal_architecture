package com.marketplace.hexagonal.Infrastructura.DTO.Admin;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateCommissionRequestDTO(
        @NotNull
        @DecimalMin(value = "0.00", message = "Rate must be >= 0")
        @DecimalMax(value = "100.00", message = "Rate must be <= 100")
        BigDecimal rate,

        String description
) {}
