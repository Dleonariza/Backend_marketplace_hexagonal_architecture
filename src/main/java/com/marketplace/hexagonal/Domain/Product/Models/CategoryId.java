package com.marketplace.hexagonal.Domain.Product.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryId {

    private final String value;

    private CategoryId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("CategoryId cannot be null or blank");
        this.value = value;
    }

    public static CategoryId generate() { return new CategoryId(UUID.randomUUID().toString()); }
    public static CategoryId of(String value) { return new CategoryId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryId)) return false;
        return value.equals(((CategoryId) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
