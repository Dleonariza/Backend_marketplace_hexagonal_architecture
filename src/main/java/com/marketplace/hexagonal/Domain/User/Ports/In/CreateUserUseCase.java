package com.marketplace.hexagonal.Domain.User.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.UserModel;

public interface CreateUserUseCase {
    UserModel createUser(UserModel user);
}
