package com.marketplace.hexagonal.Domain.Coupon.Exceptions;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(String code) {
        super("Coupon not found: " + code);
    }
}
