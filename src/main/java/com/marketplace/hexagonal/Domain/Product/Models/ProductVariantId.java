package com.marketplace.hexagonal.Domain.Product.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ProductVariantId {

    private final String value;

    private ProductVariantId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("ProductVariantId cannot be null or blank");
        this.value = value;
    }

    public static ProductVariantId generate() { return new ProductVariantId(UUID.randomUUID().toString()); }
    public static ProductVariantId of(String value) { return new ProductVariantId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariantId)) return false;
        return value.equals(((ProductVariantId) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
