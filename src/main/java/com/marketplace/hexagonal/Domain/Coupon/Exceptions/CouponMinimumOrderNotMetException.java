package com.marketplace.hexagonal.Domain.Coupon.Exceptions;

import java.math.BigDecimal;

public class CouponMinimumOrderNotMetException extends RuntimeException {
    public CouponMinimumOrderNotMetException(String code, BigDecimal required, BigDecimal actual) {
        super("Coupon \"" + code + "\" requires a minimum order of " + required +
              ". Current total: " + actual + ".");
    }
}
