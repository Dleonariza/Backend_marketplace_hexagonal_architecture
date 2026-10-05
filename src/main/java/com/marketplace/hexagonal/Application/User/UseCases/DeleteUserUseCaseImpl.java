package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Ports.In.DeleteUserUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public void delete(String id) {
        userRepositoryPort.delete(id);
    }
}
