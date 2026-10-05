package com.marketplace.hexagonal.Domain.Coupon.Exceptions;

public class CouponInactiveException extends RuntimeException {
    public CouponInactiveException(String code) {
        super("Coupon \"" + code + "\" is not currently active.");
    }
}
