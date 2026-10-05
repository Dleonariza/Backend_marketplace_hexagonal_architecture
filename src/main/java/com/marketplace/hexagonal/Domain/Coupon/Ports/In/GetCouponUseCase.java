package com.marketplace.hexagonal.Domain.Coupon.Ports.In;

import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;

public interface GetCouponUseCase {
    Coupon getCouponByCode(String code);
}
