package com.marketplace.hexagonal.Infrastructura.DTO.Auth;

public record PreRegisterResponseDTO(
        String message,
        String email
) {
    public static PreRegisterResponseDTO of(String email) {
        return new PreRegisterResponseDTO(
                "Verification code sent to " + email + ". valid for 15 minutes",
                email
        );
    }
}
