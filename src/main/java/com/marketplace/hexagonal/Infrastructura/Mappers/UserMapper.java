package com.marketplace.hexagonal.Infrastructura.Mappers;

import com.marketplace.hexagonal.Domain.User.Models.*;
import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.PermissionId;
import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.PermissionModel;
import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.RoleId;
import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.RoleModel;
import com.marketplace.hexagonal.Infrastructura.DTO.UserRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.UserResponseDTO;
import com.marketplace.hexagonal.Infrastructura.Entities.User.RoleAndPermission.PermissionEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.RoleAndPermission.RoleEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserAddressEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNoExpired", ignore = true)
    @Mapping(target = "accountNoLocked", ignore = true)
    @Mapping(target = "credentialNoExpired", ignore = true)
    UserModel toDomain(UserRequestDTO dto);

    @Mapping(source = "userId", target = "userId", qualifiedByName = "userIdToString")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    @Mapping(source = "userName", target = "userName")
    UserResponseDTO toResponse(UserModel userModel);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "userAddressEntity", ignore = true)
    UserEntity toEntity(UserModel userModel);

    default UserModel toDomain(UserEntity entity) {
        if (entity == null) return null;

        return UserModel.reconstitute(
                UserId.of(entity.getId().toString()),
                entity.getEmail(),
                entity.getUserName(),
                entity.getPassword(),
                entity.getName(),
                entity.getAvatarUrl(),
                entity.getPhone(),
                mapAddresses(entity),
                entity.getStatus(),
                mapRoles(entity),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isEnabled(),
                entity.isAccountNoExpired(),
                entity.isAccountNoLocked(),
                entity.isCredentialNoExpired()
        );
    }

    default Set<RoleModel> mapRoles(UserEntity entity) {
        if (entity.getRoles() == null || entity.getRoles().isEmpty()) return new HashSet<>();

        Set<RoleModel> roles = new HashSet<>();
        for (RoleEntity roleEntity : entity.getRoles()) {
            roles.add(RoleModel.builder()
                    .id(RoleId.of(roleEntity.getId().toString()))
                    .role(roleEntity.getRole())
                    .permissions(mapPermissions(roleEntity))
                    .build());
        }
        return roles;
    }

    default Set<PermissionModel> mapPermissions(RoleEntity roleEntity) {
        if (roleEntity.getPermissions() == null || roleEntity.getPermissions().isEmpty()) return new HashSet<>();

        Set<PermissionModel> permissions = new HashSet<>();
        for (PermissionEntity permEntity : roleEntity.getPermissions()) {
            permissions.add(PermissionModel.builder()
                    .id(PermissionId.of(permEntity.getId().toString()))
                    .permission(permEntity.getPermission())
                    .build());
        }
        return permissions;
    }

    default List<UserAddress> mapAddresses(UserEntity entity) {
        if (entity.getUserAddressEntity() == null || entity.getUserAddressEntity().isEmpty()) return new ArrayList<>();

        List<UserAddress> result = new ArrayList<>();
        for (UserAddressEntity addr : entity.getUserAddressEntity()) {
            result.add(UserAddress.builder()
                    .id(UserAddressId.of(addr.getId().toString()))
                    .street(addr.getStreet())
                    .city(addr.getCity())
                    .state(addr.getState())
                    .postalCode(addr.getPostalCode())
                    .country(addr.getCountry())
                    .isDefault(addr.getIsDefault())
                    .build());
        }
        return result;
    }

    @Named("userIdToString")
    default String userIdToString(UserId userId) {
        return userId == null ? null : userId.getValue();
    }

    @Named("userIdToUUID")
    default UUID userIdToUUID(UserId userId) {
        return userId == null ? null : UUID.fromString(userId.getValue());
    }

    @Named("statusToString")
    default String statusToString(UserStatus status) {
        return status == null ? null : status.name();
    }

    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<RoleModel> roles) {
        if (roles == null) return new HashSet<>();
        return roles.stream()
                .map(r -> r.getRole().name())
                .collect(Collectors.toSet());
    }
}
