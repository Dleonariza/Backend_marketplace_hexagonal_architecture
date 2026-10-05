package com.marketplace.hexagonal.Domain.Auth.Ports.Out;

import com.marketplace.hexagonal.Domain.Auth.Models.PendingRegistration;

import java.util.Optional;

public interface PendingRegistrationRepositoryPort {
    PendingRegistration save(PendingRegistration pending);
    Optional<PendingRegistration> findByEmail(String email);
    void delete(PendingRegistration pending);
}
