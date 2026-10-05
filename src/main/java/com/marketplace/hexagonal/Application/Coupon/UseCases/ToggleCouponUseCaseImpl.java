package com.marketplace.hexagonal.Application.Coupon.UseCases;

import com.marketplace.hexagonal.Domain.Coupon.Exceptions.CouponNotFoundException;
import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Ports.In.ToggleCouponUseCase;
import com.marketplace.hexagonal.Domain.Coupon.Ports.Out.CouponRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ToggleCouponUseCaseImpl implements ToggleCouponUseCase {

    private final CouponRepositoryPort couponRepo;

    @Override
    public void activateCoupon(String code) {
        Coupon coupon = findOrThrow(code);
        coupon.activate();
        couponRepo.save(coupon);
    }

    @Override
    public void deactivateCoupon(String code) {
        Coupon coupon = findOrThrow(code);
        coupon.deactivate();
        couponRepo.save(coupon);
    }

    private Coupon findOrThrow(String code) {
        return couponRepo.findByCode(code.toUpperCase().trim())
                .orElseThrow(() -> new CouponNotFoundException(code));
    }
}
