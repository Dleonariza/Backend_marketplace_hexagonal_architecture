package com.marketplace.hexagonal.Infrastructura.DTO.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequestDTO(
        @NotBlank @Size(min = 2, max = 100) String name,
        @Size(max = 1000) String description,
        @NotBlank @Size(min = 2, max = 150) String slug
) {}
