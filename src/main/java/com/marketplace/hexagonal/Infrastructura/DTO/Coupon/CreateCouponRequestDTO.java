package com.marketplace.hexagonal.Infrastructura.DTO.Coupon;

import com.marketplace.hexagonal.Domain.Coupon.Models.DiscountType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateCouponRequestDTO(
        @NotBlank String code,
        String description,
        @NotNull DiscountType discountType,
        @NotNull @Positive BigDecimal discountValue,
        @Positive BigDecimal minimumOrderAmount,
        @Positive Integer maxUses,
        Instant validFrom,
        Instant validUntil
) {}
