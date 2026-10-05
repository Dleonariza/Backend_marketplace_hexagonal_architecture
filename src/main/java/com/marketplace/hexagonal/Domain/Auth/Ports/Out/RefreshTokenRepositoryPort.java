package com.marketplace.hexagonal.Domain.Auth.Ports.Out;

import com.marketplace.hexagonal.Domain.Auth.Models.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteByTokenHash(String tokenHash);
    void deleteAllByUserEmail(String userEmail);
}
