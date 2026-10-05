package com.marketplace.hexagonal.Domain.Auth.Models;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthTokens {

    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    private String email;
    private List<String> roles;
    private String userId;
    private String name;
    private String userName;
    private String avatarUrl;
}
