package com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
public class PermissionId {
    private String value;

    private PermissionId(String value){
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("PermissionId can't null or blank");
        this.value = value;
    }

    public static PermissionId generate() {
        return new PermissionId(UUID.randomUUID().toString());
    }

    public static PermissionId of(String value){
        return new PermissionId(value);
    }
}
