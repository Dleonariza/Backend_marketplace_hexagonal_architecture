package com.marketplace.hexagonal.Infrastructura.DTO.Coupon;

import com.marketplace.hexagonal.Domain.Coupon.Models.AppliedCoupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.DiscountType;

import java.math.BigDecimal;

public record AppliedCouponDTO(
        String couponCode,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal discountAmount,
        String currency
) {
    public static AppliedCouponDTO from(AppliedCoupon ac) {
        if (ac == null) return null;
        return new AppliedCouponDTO(
                ac.couponCode(),
                ac.discountType(),
                ac.discountValue(),
                ac.discountAmount().amount(),
                ac.discountAmount().currency());
    }
}
