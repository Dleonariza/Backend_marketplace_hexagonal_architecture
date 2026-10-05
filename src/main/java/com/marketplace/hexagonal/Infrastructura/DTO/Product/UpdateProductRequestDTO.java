package com.marketplace.hexagonal.Infrastructura.DTO.Product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateProductRequestDTO(
        @NotBlank String categoryId,
        @NotNull String productType,
        @NotBlank @Size(min = 2, max = 200) String name,
        @Size(max = 5000) String description,
        @NotBlank @Size(min = 2, max = 220) String slug,
        @NotNull @DecimalMin("0.00") BigDecimal basePriceAmount,
        @NotBlank @Size(min = 3, max = 3) String basePriceCurrency
) {}
