package com.marketplace.hexagonal.Domain.Auth.Ports.In;

public interface PreRegisterUseCase {
    String preRegister(String email, String userName, String rawPassword, String name, String phone);
}
