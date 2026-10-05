package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.In.UpdateUserProfileUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateUserProfileUseCaseImpl implements UpdateUserProfileUseCase {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    public UserModel updateProfile(UserId userId, String name, String phone, String avatarUrl) {
        return userRepositoryPort.updateProfile(userId, name, phone, avatarUrl);
    }
}
