package com.marketplace.hexagonal.Domain.Product.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ProductImageId {

    private final String value;

    private ProductImageId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("ProductImageId cannot be null or blank");
        this.value = value;
    }

    public static ProductImageId generate() { return new ProductImageId(UUID.randomUUID().toString()); }
    public static ProductImageId of(String value) { return new ProductImageId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductImageId)) return false;
        return value.equals(((ProductImageId) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
