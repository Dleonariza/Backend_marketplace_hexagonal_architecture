package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.In.FindAllUsersUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class FindAllUseCaseImpl implements FindAllUsersUseCase {
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public List<UserModel> findAll() {
        return userRepositoryPort.findAll();
    }
}
