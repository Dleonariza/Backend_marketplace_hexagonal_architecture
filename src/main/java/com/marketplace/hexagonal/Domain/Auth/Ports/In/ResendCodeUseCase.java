package com.marketplace.hexagonal.Domain.Auth.Ports.In;

public interface ResendCodeUseCase {
    String resendCode(String email);
}
