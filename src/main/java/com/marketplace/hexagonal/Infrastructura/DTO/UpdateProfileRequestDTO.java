package com.marketplace.hexagonal.Infrastructura.DTO;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequestDTO(

        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @Size(min = 10, message = "Phone must have at least 10 characters")
        String phone,

        String avatarUrl
) {}
