package com.marketplace.hexagonal.Application.User.Services;

import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.In.*;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserStatus;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class UserService implements CreateUserUseCase, FindUserByNameUseCase, FindAllUsersUseCase,
        FindUserByIdUseCase, DeleteUserUseCase, UpdateUserProfileUseCase,
        SearchUsersUseCase, BlockUserUseCase, UnblockUserUseCase {
    private final CreateUserUseCase createUserUseCase;
    private final FindUserByNameUseCase findUserByNameUseCase;
    private final FindAllUsersUseCase findAllUsersUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final SearchUsersUseCase searchUsersUseCase;
    private final BlockUserUseCase blockUserUseCase;
    private final UnblockUserUseCase unblockUserUseCase;

    private final PasswordEncoder passwordEncoder;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public UserModel createUser(UserModel user) {
        List<UserAddress> userAddresses = user.getAddresses() == null
                ? new ArrayList<>()
                : user.getAddresses().stream()
                        .map(addr -> UserAddress.builder()
                                .id(UserAddressId.generate())
                                .street(addr.getStreet())
                                .city(addr.getCity())
                                .state(addr.getState())
                                .postalCode(addr.getPostalCode())
                                .country(addr.getCountry())
                                .isDefault(addr.getIsDefault())
                                .build())
                        .toList();

        return createUserUseCase.createUser(UserModel.create(
                user.getEmail(),
                user.getUserName(),
                passwordEncoder.encode(user.getPassword()),
                user.getName(),
                user.getAvatarUrl(),
                user.getPhone(),
                userAddresses));
    }

    public UserModel createUserPreEncoded(UserModel user) {
        return createUserUseCase.createUser(user);
    }

    @Override
    public UserModel updateProfile(UserId userId, String name, String phone, String avatarUrl) {
        return updateUserProfileUseCase.updateProfile(userId, name, phone, avatarUrl);
    }

    public Optional<UserModel> findByEmail(String email) {
        return userRepositoryPort.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepositoryPort.existsByEmail(email);
    }

    public boolean existsByUserName(String userName) {
        return userRepositoryPort.existsByUserName(userName);
    }

    public void updatePassword(String email, String encodedPassword) {
        userRepositoryPort.updatePassword(email, encodedPassword);
    }

    @Override
    public void delete(String id) {
        deleteUserUseCase.delete(id);
    }

    @Override
    public List<UserModel> findAll() {
        return findAllUsersUseCase.findAll();
    }

    @Override
    public UserModel findByName(String name) {
        return findUserByNameUseCase.findByName(name);
    }

    @Override
    public UserModel findById(UserId id) {
        return findUserByIdUseCase.findById(id);
    }

    @Override
    public List<UserModel> search(String keyword) {
        return searchUsersUseCase.search(keyword);
    }

    @Override
    public UserModel block(UserId userId) {
        return blockUserUseCase.block(userId);
    }

    @Override
    public UserModel unblock(UserId userId) {
        return unblockUserUseCase.unblock(userId);
    }

    public List<UserModel> findByStatus(UserStatus status) {
        return userRepositoryPort.findAll().stream()
                .filter(u -> u.getStatus() == status)
                .toList();
    }
}
