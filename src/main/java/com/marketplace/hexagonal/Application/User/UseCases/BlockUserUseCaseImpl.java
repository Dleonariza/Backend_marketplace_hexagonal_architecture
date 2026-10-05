package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.In.BlockUserUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BlockUserUseCaseImpl implements BlockUserUseCase {

    private final UserRepositoryPort userRepo;

    @Override
    public UserModel block(UserId userId) {
        return userRepo.blockUser(userId);
    }
}
