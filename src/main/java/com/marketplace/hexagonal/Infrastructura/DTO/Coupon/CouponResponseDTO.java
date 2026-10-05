package com.marketplace.hexagonal.Infrastructura.DTO.Coupon;

import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.DiscountType;

import java.math.BigDecimal;
import java.time.Instant;

public record CouponResponseDTO(
        String id,
        String code,
        String description,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal minimumOrderAmount,
        Integer maxUses,
        int usedCount,
        Instant validFrom,
        Instant validUntil,
        boolean active,
        Instant createdAt
) {
    public static CouponResponseDTO from(Coupon coupon) {
        return new CouponResponseDTO(
                coupon.getId().getValue(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinimumOrderAmount(),
                coupon.getMaxUses(),
                coupon.getUsedCount(),
                coupon.getValidFrom(),
                coupon.getValidUntil(),
                coupon.isActive(),
                coupon.getCreatedAt());
    }
}
