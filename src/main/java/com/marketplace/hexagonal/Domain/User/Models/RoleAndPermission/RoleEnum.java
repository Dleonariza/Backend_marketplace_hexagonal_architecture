package com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission;

import java.util.Set;
import java.util.stream.Collectors;

public enum RoleEnum {
    ADMIN,
    BUYER,
    SELLER,
    DEVELOPER,
    SUPPORT;

    public static Set<PermissionModel> getDefaultPermissionsFor(RoleEnum role) {
        Set<PermissionEnum> perms = switch (role) {
            case BUYER -> Set.of(
                    PermissionEnum.READ_PRODUCTS,
                    PermissionEnum.READ_ORDERS,
                    PermissionEnum.WRITE_ORDERS
            );
            case SELLER -> Set.of(
                    PermissionEnum.READ_PRODUCTS,
                    PermissionEnum.WRITE_PRODUCTS,
                    PermissionEnum.DELETE_PRODUCTS,
                    PermissionEnum.READ_ORDERS
            );
            case SUPPORT -> Set.of(
                    PermissionEnum.READ_TICKETS,
                    PermissionEnum.WRITE_TICKETS,
                    PermissionEnum.READ_ORDERS
            );
            case DEVELOPER, ADMIN -> Set.of(PermissionEnum.values());
        };

        return perms.stream()
                .map(perm -> PermissionModel.builder()
                        .id(PermissionId.generate())
                        .permission(perm)
                        .build())
                .collect(Collectors.toSet());
    }
}
