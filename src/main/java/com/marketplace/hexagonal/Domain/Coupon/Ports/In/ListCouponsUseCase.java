package com.marketplace.hexagonal.Domain.Coupon.Ports.In;

import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;

import java.util.List;

public interface ListCouponsUseCase {
    List<Coupon> listAllCoupons();
}
