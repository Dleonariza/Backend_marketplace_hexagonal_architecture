package com.marketplace.hexagonal.Domain.Coupon.Exceptions;

public class CouponMaxUsesReachedException extends RuntimeException {
    public CouponMaxUsesReachedException(String code) {
        super("Coupon \"" + code + "\" has reached its maximum number of uses.");
    }
}
