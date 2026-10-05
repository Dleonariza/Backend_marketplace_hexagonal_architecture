package com.marketplace.hexagonal.Domain.Seller.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SellerProfileId {

    private final String value;

    private SellerProfileId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("SellerProfileId cannot be null or blank");
        this.value = value;
    }

    public static SellerProfileId generate() {
        return new SellerProfileId(UUID.randomUUID().toString());
    }

    public static SellerProfileId of(String value) {
        return new SellerProfileId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SellerProfileId)) return false;
        return value.equals(((SellerProfileId) o).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
