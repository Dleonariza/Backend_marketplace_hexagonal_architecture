package com.marketplace.hexagonal.Domain.Auth.Ports.In;

public interface ForgotPasswordUseCase {
    void forgotPassword(String email);
}
