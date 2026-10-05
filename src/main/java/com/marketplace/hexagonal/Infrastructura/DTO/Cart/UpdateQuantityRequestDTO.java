package com.marketplace.hexagonal.Infrastructura.DTO.Cart;

import jakarta.validation.constraints.Min;

public record UpdateQuantityRequestDTO(@Min(1) int quantity) {}
