package com.marketplace.hexagonal.Application.Coupon.UseCases;

import com.marketplace.hexagonal.Domain.Coupon.Exceptions.CouponNotFoundException;
import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Ports.In.GetCouponUseCase;
import com.marketplace.hexagonal.Domain.Coupon.Ports.Out.CouponRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetCouponUseCaseImpl implements GetCouponUseCase {

    private final CouponRepositoryPort couponRepo;

    @Override
    public Coupon getCouponByCode(String code) {
        return couponRepo.findByCode(code.toUpperCase().trim())
                .orElseThrow(() -> new CouponNotFoundException(code));
    }
}
