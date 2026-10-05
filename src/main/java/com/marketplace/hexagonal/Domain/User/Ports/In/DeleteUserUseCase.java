package com.marketplace.hexagonal.Domain.User.Ports.In;

public interface DeleteUserUseCase {
    void delete(String id);
}
