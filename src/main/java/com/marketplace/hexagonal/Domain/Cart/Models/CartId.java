package com.marketplace.hexagonal.Domain.Cart.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CartId {

    private final String value;

    private CartId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("CartId cannot be null or blank");
        this.value = value;
    }

    public static CartId generate() { return new CartId(UUID.randomUUID().toString()); }
    public static CartId of(String value) { return new CartId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartId)) return false;
        return value.equals(((CartId) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
