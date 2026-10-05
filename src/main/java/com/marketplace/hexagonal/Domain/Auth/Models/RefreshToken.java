package com.marketplace.hexagonal.Domain.Auth.Models;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    private String tokenHash;
    private String userEmail;
    private Instant expiresAt;
    private Instant createdAt;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
