package com.marketplace.hexagonal.Application.Coupon.UseCases;

import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.DiscountType;
import com.marketplace.hexagonal.Domain.Coupon.Ports.In.CreateCouponUseCase;
import com.marketplace.hexagonal.Domain.Coupon.Ports.Out.CouponRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@RequiredArgsConstructor
public class CreateCouponUseCaseImpl implements CreateCouponUseCase {

    private final CouponRepositoryPort couponRepo;

    @Override
    public Coupon createCoupon(String code, String description, DiscountType discountType,
                               BigDecimal discountValue, BigDecimal minimumOrderAmount,
                               Integer maxUses, Instant validFrom, Instant validUntil) {
        Coupon coupon = Coupon.create(
                code, description, discountType,
                discountValue, minimumOrderAmount,
                maxUses, validFrom, validUntil);
        return couponRepo.save(coupon);
    }
}
