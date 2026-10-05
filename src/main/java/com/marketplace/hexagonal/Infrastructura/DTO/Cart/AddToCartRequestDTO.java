package com.marketplace.hexagonal.Infrastructura.DTO.Cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddToCartRequestDTO(
        @NotBlank String productId,
        String variantId,
        @Min(1) int quantity
) {}
