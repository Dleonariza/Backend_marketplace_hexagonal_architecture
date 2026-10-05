package com.marketplace.hexagonal.Application.Coupon.UseCases;

import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Ports.In.ListCouponsUseCase;
import com.marketplace.hexagonal.Domain.Coupon.Ports.Out.CouponRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListCouponsUseCaseImpl implements ListCouponsUseCase {

    private final CouponRepositoryPort couponRepo;

    @Override
    public List<Coupon> listAllCoupons() {
        return couponRepo.findAll();
    }
}
