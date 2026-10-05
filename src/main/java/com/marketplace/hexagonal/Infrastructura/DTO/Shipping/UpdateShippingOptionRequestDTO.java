package com.marketplace.hexagonal.Infrastructura.DTO.Shipping;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateShippingOptionRequestDTO(
        @NotBlank String name,
                                         String description,
        @NotNull @PositiveOrZero BigDecimal price,
        @NotNull String currency,
        @NotNull @Min(0) Integer estimatedDaysMin,
        @NotNull @Min(0) Integer estimatedDaysMax
) {}
