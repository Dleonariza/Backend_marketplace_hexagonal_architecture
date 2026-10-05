package com.marketplace.hexagonal.Infrastructura.DTO.Product;

import jakarta.validation.constraints.NotBlank;

public record AddImageRequestDTO(
        @NotBlank String url,
        String altText
) {}
