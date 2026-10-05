package com.marketplace.hexagonal.Domain.User.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.UserModel;

public interface FindUserByNameUseCase {
    UserModel findByName(String name);
}
