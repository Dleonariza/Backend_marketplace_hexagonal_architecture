package com.marketplace.hexagonal.Domain.User.Ports.Out;

import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.RoleEnum;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    UserModel save(UserModel user);
    UserModel findByName(String name);
    List<UserModel> findAll();
    void delete(String id);
    UserModel findById(UserId id);
    Optional<UserModel> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
    void updatePassword(String email, String encodedPassword);
    UserModel updateProfile(UserId userId, String name, String phone, String avatarUrl);

    void addRole(UserId userId, RoleEnum role);

    List<UserModel> searchUsers(String keyword);

    UserModel blockUser(UserId userId);

    UserModel unblockUser(UserId userId);
}
