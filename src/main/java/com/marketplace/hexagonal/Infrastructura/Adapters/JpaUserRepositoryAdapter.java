package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.RoleEnum;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Entities.User.RoleAndPermission.RoleEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import com.marketplace.hexagonal.Infrastructura.Mappers.UserMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaRoleRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;
    private final JpaRoleRepository jpaRoleRepository;
    private final UserMapper userMapper;

    @Override
    public UserModel save(UserModel user) {
        UserEntity userEntity = userMapper.toEntity(user);

        Set<RoleEntity> roleEntities = user.getRoles().stream()
                .map(roleModel -> jpaRoleRepository.findByRole(roleModel.getRole())
                        .orElseThrow(() -> new IllegalStateException(
                                "Rol not found in DB: " + roleModel.getRole() +
                                ". Make sure that the DataInitializer has inserted the roles.")))
                .collect(Collectors.toSet());

        userEntity.setRoles(roleEntities);

        UserEntity saved = jpaUserRepository.save(userEntity);
        return userMapper.toDomain(saved);
    }

    @Override
    public UserModel findByName(String name) {
        return null;
    }

    @Override
    public List<UserModel> findAll() {
        return jpaUserRepository.findAll().stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        UUID uuid = UUID.fromString(id);
        if (!jpaUserRepository.existsById(uuid)) {
            throw new UserNotFoundException(UserId.of(id));
        }
        jpaUserRepository.deleteById(uuid);
    }

    @Override
    @Transactional(readOnly = true)
    public UserModel findById(UserId id) {
        return jpaUserRepository.findById(UUID.fromString(id.getValue()))
                .map(userMapper::toDomain)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserModel> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return jpaUserRepository.existsByUserName(userName);
    }

    @Override
    public void updatePassword(String email, String encodedPassword) {
        jpaUserRepository.updatePassword(email, encodedPassword);
    }

    @Override
    public void addRole(UserId userId, RoleEnum role) {
        UserEntity user = jpaUserRepository.findById(UUID.fromString(userId.getValue()))
                .orElseThrow(() -> new UserNotFoundException(userId));
        RoleEntity roleEntity = jpaRoleRepository.findByRole(role)
                .orElseThrow(() -> new IllegalStateException(
                        "Role not found in DB: " + role +
                        ". Make sure the DataInitializer has inserted this role."));
        user.getRoles().add(roleEntity);
        jpaUserRepository.save(user);
    }

    @Override
    public List<UserModel> searchUsers(String keyword) {
        return jpaUserRepository.searchByNameOrEmail(keyword).stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public UserModel blockUser(UserId userId) {
        UserEntity entity = jpaUserRepository.findById(UUID.fromString(userId.getValue()))
                .orElseThrow(() -> new UserNotFoundException(userId));
        UserModel domain = userMapper.toDomain(entity);
        domain.block();
        entity.setStatus(domain.getStatus());
        entity.setAccountNoLocked(domain.isAccountNoLocked());
        return userMapper.toDomain(jpaUserRepository.save(entity));
    }

    @Override
    public UserModel unblockUser(UserId userId) {
        UserEntity entity = jpaUserRepository.findById(UUID.fromString(userId.getValue()))
                .orElseThrow(() -> new UserNotFoundException(userId));
        UserModel domain = userMapper.toDomain(entity);
        domain.unblock();
        entity.setStatus(domain.getStatus());
        entity.setAccountNoLocked(domain.isAccountNoLocked());
        return userMapper.toDomain(jpaUserRepository.save(entity));
    }

    @Override
    public UserModel updateProfile(UserId userId, String name, String phone, String avatarUrl) {
        UserEntity entity = jpaUserRepository.findById(UUID.fromString(userId.getValue()))
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (name != null && !name.isBlank()) entity.setName(name.strip());
        if (phone != null && !phone.isBlank()) entity.setPhone(phone.strip());
        if (avatarUrl != null) entity.setAvatarUrl(avatarUrl.isBlank() ? null : avatarUrl.strip());

        return userMapper.toDomain(jpaUserRepository.save(entity));
    }
}
