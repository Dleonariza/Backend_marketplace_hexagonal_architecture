package com.marketplace.hexagonal.Domain.Cart.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CartItemId {

    private final String value;

    private CartItemId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("CartItemId cannot be null or blank");
        this.value = value;
    }

    public static CartItemId generate() { return new CartItemId(UUID.randomUUID().toString()); }
    public static CartItemId of(String value) { return new CartItemId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItemId)) return false;
        return value.equals(((CartItemId) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
