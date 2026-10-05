package com.marketplace.hexagonal.Infrastructura.DTO.Auth;

import java.util.List;

public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,

        String userId,
        String email,
        String name,
        String userName,
        String avatarUrl,
        List<String> roles
) {
    public static LoginResponseDTO of(String accessToken, String refreshToken,
                                      long expiresIn, String email, List<String> roles,
                                      String userId, String name, String userName, String avatarUrl) {
        return new LoginResponseDTO(accessToken, refreshToken, "Bearer", expiresIn,
                userId, email, name, userName, avatarUrl, roles);
    }

    public static LoginResponseDTO ofWeb(String accessToken, long expiresIn,
                                         String email, List<String> roles,
                                         String userId, String name, String userName, String avatarUrl) {
        return new LoginResponseDTO(accessToken, null, "Bearer", expiresIn,
                userId, email, name, userName, avatarUrl, roles);
    }
}
