package com.marketplace.hexagonal.Infrastructura.DTO.Auth;

public record TokenRefreshResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
    public static TokenRefreshResponseDTO of(String accessToken, String refreshToken, long expiresIn) {
        return new TokenRefreshResponseDTO(accessToken, refreshToken, "Bearer", expiresIn);
    }

    public static TokenRefreshResponseDTO ofWeb(String accessToken, long expiresIn) {
        return new TokenRefreshResponseDTO(accessToken, null, "Bearer", expiresIn);
    }
}
