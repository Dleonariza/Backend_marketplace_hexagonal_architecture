package com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
public class RoleId {
    private String value;

    private RoleId(String value){
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("RoleId can't null or blank");
        this.value = value;
    }

    public static RoleId generate() {
        return new RoleId(UUID.randomUUID().toString());
    }

    public static RoleId of(String value) {
        return new RoleId(value);
    }
}
