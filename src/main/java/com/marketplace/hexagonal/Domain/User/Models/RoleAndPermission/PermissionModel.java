package com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class PermissionModel {
    private PermissionId id;
    private PermissionEnum permission;
}
