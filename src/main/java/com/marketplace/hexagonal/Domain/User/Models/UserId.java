package com.marketplace.hexagonal.Domain.User.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserId {
    private final String value;

    private UserId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("UserId can't null or blank");
        this.value = value;
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    public static UserId of(String value) {
        return new UserId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        return value.equals(((UserId) o).value);
    }
}
