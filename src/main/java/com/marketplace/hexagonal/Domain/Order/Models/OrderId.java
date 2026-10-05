package com.marketplace.hexagonal.Domain.Order.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderId {

    private final String value;

    private OrderId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("OrderId cannot be null or blank");
        this.value = value;
    }

    public static OrderId generate() { return new OrderId(UUID.randomUUID().toString()); }
    public static OrderId of(String value) { return new OrderId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderId)) return false;
        return value.equals(((OrderId) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
