package com.marketplace.hexagonal.Domain.Auth.Ports.In;

public interface LogoutUseCase {
    void logout(String rawRefreshToken);
}
