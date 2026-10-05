package com.marketplace.hexagonal.Domain.Product.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ProductId {

    private final String value;

    private ProductId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("ProductId cannot be null or blank");
        this.value = value;
    }

    public static ProductId generate() { return new ProductId(UUID.randomUUID().toString()); }
    public static ProductId of(String value) { return new ProductId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductId)) return false;
        return value.equals(((ProductId) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
