package com.marketplace.hexagonal.Domain.Coupon.Models;

import com.marketplace.hexagonal.Domain.Product.Models.Money;

import java.math.BigDecimal;

public record AppliedCoupon(
        String couponCode,
        DiscountType discountType,
        BigDecimal discountValue,
        Money discountAmount
) {}
