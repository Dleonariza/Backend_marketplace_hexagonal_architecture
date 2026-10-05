package com.marketplace.hexagonal.Domain.Favorite.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class FavoriteId {
    private final String value;

    public FavoriteId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("FavoriteId cannot be null or blank");
        this.value = value;
    }

    public static FavoriteId generate() { return new FavoriteId(UUID.randomUUID().toString()); }
    public static FavoriteId of(String value) { return new FavoriteId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoriteId)) return false;
        return value.equals(((FavoriteId) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
