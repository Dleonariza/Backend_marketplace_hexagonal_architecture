package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.In.FindUserByIdUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindUserByIdUseCaseImpl implements FindUserByIdUseCase {
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public UserModel findById(UserId id) {
        return userRepositoryPort.findById(id);
    }
}
