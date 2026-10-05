package com.marketplace.hexagonal.Domain.Auth.Ports.In;

public interface ResetPasswordUseCase {
    void resetPassword(String email, String code, String newPassword);
}
