package com.marketplace.hexagonal.Domain.Coupon.Models;

import java.util.Objects;
import java.util.UUID;

public final class CouponId {

    private final String value;

    private CouponId(String value) { this.value = value; }

    public static CouponId generate() { return new CouponId(UUID.randomUUID().toString()); }
    public static CouponId of(String value) {
        Objects.requireNonNull(value, "CouponId value cannot be null");
        return new CouponId(value);
    }

    public String getValue() { return value; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CouponId c)) return false;
        return value.equals(c.value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
