package com.marketplace.hexagonal.Infrastructura.DTO.Product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Map;

public record UpdateVariantRequestDTO(
        @NotBlank @Size(max = 100) String sku,
        Map<String, String> attributes,
        @DecimalMin("0.00") BigDecimal priceAmount,
        @Size(min = 3, max = 3) String priceCurrency,
        @Min(0) int stock
) {}
