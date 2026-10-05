package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.In.SearchUsersUseCase;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SearchUsersUseCaseImpl implements SearchUsersUseCase {

    private final UserRepositoryPort userRepo;

    @Override
    public List<UserModel> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return userRepo.findAll();
        return userRepo.searchUsers(keyword.strip());
    }
}
