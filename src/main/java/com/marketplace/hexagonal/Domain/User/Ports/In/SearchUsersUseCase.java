package com.marketplace.hexagonal.Domain.User.Ports.In;

import com.marketplace.hexagonal.Domain.User.Models.UserModel;

import java.util.List;

public interface SearchUsersUseCase {
    List<UserModel> search(String keyword);
}
