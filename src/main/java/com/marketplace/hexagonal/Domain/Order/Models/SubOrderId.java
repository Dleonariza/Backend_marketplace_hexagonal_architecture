package com.marketplace.hexagonal.Domain.Order.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SubOrderId {

    private final String value;

    private SubOrderId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("SubOrderId cannot be null or blank");
        this.value = value;
    }

    public static SubOrderId generate() { return new SubOrderId(UUID.randomUUID().toString()); }
    public static SubOrderId of(String value) { return new SubOrderId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubOrderId)) return false;
        return value.equals(((SubOrderId) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
