package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.In.UnblockUserUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UnblockUserUseCaseImpl implements UnblockUserUseCase {

    private final UserRepositoryPort userRepo;

    @Override
    public UserModel unblock(UserId userId) {
        return userRepo.unblockUser(userId);
    }
}
