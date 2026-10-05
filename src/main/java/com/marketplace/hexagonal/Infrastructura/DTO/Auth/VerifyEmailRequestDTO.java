package com.marketplace.hexagonal.Infrastructura.DTO.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyEmailRequestDTO(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "\\d{6}", message = "The code must be 6 digits") String code
) {}
