package com.marketplace.hexagonal.Domain.Order.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderItemId {

    private final String value;

    private OrderItemId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("OrderItemId cannot be null or blank");
        this.value = value;
    }

    public static OrderItemId generate() { return new OrderItemId(UUID.randomUUID().toString()); }
    public static OrderItemId of(String value) { return new OrderItemId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItemId)) return false;
        return value.equals(((OrderItemId) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
