package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.In.FindUserByNameUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindUserByNameUseCaseImpl implements FindUserByNameUseCase {
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public UserModel findByName(String name) {
        return userRepositoryPort.findByName(name);
    }
}
