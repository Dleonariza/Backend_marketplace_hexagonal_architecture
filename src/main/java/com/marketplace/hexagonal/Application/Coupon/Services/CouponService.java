package com.marketplace.hexagonal.Application.Coupon.Services;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.DiscountType;
import com.marketplace.hexagonal.Domain.Coupon.Ports.In.*;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
public class CouponService implements
        CreateCouponUseCase,
        GetCouponUseCase,
        ListCouponsUseCase,
        ToggleCouponUseCase,
        ApplyCouponUseCase,
        RemoveCouponUseCase {
    private final CreateCouponUseCase createCouponUseCase;
    private final GetCouponUseCase getCouponUseCase;
    private final ListCouponsUseCase listCouponsUseCase;
    private final ToggleCouponUseCase toggleCouponUseCase;
    private final ApplyCouponUseCase applyCouponUseCase;
    private final RemoveCouponUseCase removeCouponUseCase;

    @Override
    public Coupon createCoupon(String code, String description, DiscountType discountType,
                               BigDecimal discountValue, BigDecimal minimumOrderAmount,
                               Integer maxUses, Instant validFrom, Instant validUntil) {
        return createCouponUseCase.createCoupon(
                code, description, discountType,
                discountValue, minimumOrderAmount,
                maxUses, validFrom, validUntil);
    }

    @Override
    public Coupon getCouponByCode(String code) {
        return getCouponUseCase.getCouponByCode(code);
    }

    @Override
    public List<Coupon> listAllCoupons() {
        return listCouponsUseCase.listAllCoupons();
    }

    @Override
    public void activateCoupon(String code) {
        toggleCouponUseCase.activateCoupon(code);
    }

    @Override
    public void deactivateCoupon(String code) {
        toggleCouponUseCase.deactivateCoupon(code);
    }

    @Override
    public Cart applyCoupon(UserId userId, String sessionId, String couponCode) {
        return applyCouponUseCase.applyCoupon(userId, sessionId, couponCode);
    }

    @Override
    public Cart removeCoupon(UserId userId, String sessionId) {
        return removeCouponUseCase.removeCoupon(userId, sessionId);
    }
}
