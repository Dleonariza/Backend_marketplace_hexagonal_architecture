package com.marketplace.hexagonal.Domain.Coupon.Ports.In;

import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.DiscountType;

import java.math.BigDecimal;
import java.time.Instant;

public interface CreateCouponUseCase {
    Coupon createCoupon(String code, String description, DiscountType discountType,
                        BigDecimal discountValue, BigDecimal minimumOrderAmount,
                        Integer maxUses, Instant validFrom, Instant validUntil);
}
