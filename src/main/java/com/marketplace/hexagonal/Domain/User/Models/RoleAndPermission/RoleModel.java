package com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoleModel {
    private RoleId id;
    private RoleEnum role;
    private Set<PermissionModel> permissions = new HashSet<>();
}
