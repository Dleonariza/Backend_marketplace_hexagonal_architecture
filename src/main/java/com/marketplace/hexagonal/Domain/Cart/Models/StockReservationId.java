package com.marketplace.hexagonal.Domain.Cart.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class StockReservationId {

    private final String value;

    private StockReservationId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("StockReservationId cannot be null or blank");
        this.value = value;
    }

    public static StockReservationId generate() { return new StockReservationId(UUID.randomUUID().toString()); }
    public static StockReservationId of(String value) { return new StockReservationId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockReservationId)) return false;
        return value.equals(((StockReservationId) o).value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
