package com.marketplace.hexagonal.Infrastructura.DTO.Auth;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequestDTO(
        @NotBlank String refreshToken
) {}
