package com.marketplace.hexagonal.Domain.Shipping.Models;

import java.util.UUID;

public class ShippingOptionId {

    private final String value;

    private ShippingOptionId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("ShippingOptionId cannot be null or blank");
        this.value = value;
    }

    public static ShippingOptionId generate() {
        return new ShippingOptionId(UUID.randomUUID().toString());
    }

    public static ShippingOptionId of(String value) {
        return new ShippingOptionId(value);
    }

    public String getValue() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShippingOptionId)) return false;
        return value.equals(((ShippingOptionId) o).value);
    }

    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
