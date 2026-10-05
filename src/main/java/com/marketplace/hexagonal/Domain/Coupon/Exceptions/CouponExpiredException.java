package com.marketplace.hexagonal.Domain.Coupon.Exceptions;

public class CouponExpiredException extends RuntimeException {
    public CouponExpiredException(String code) {
        super("Coupon \"" + code + "\" has expired.");
    }
}
