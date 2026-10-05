package com.marketplace.hexagonal.Domain.User.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;

public interface FindUserByIdUseCase {
    UserModel findById(UserId id);
}
