package com.marketplace.hexagonal.Domain.Auth.Ports.In;

import com.marketplace.hexagonal.Domain.Auth.Models.AuthTokens;

public interface LoginUseCase {
    AuthTokens login(String email, String password);
}
