package com.marketplace.hexagonal.Infrastructura.DTO.User;

import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserStatus;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record AdminUserDetailDTO(
        String userId,
        String email,
        String userName,
        String name,
        String phone,
        String avatarUrl,
        UserStatus status,
        Set<String> roles,
        Instant createdAt
) {
    public static AdminUserDetailDTO from(UserModel user) {
        Set<String> roleNames = user.getRoles() == null
                ? Set.of()
                : user.getRoles().stream()
                        .map(r -> r.getRole().name())
                        .collect(Collectors.toSet());

        return new AdminUserDetailDTO(
                user.getUserId().getValue(),
                user.getEmail(),
                user.getUserName(),
                user.getName(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getStatus(),
                roleNames,
                user.getCreatedAt()
        );
    }
}
