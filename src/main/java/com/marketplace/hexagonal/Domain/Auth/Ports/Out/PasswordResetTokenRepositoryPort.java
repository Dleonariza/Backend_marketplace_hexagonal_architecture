package com.marketplace.hexagonal.Domain.Auth.Ports.Out;

import com.marketplace.hexagonal.Domain.Auth.Models.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepositoryPort {
    PasswordResetToken save(PasswordResetToken token);
    Optional<PasswordResetToken> findByEmail(String email);
    void delete(PasswordResetToken token);
}
