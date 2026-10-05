package com.marketplace.hexagonal.Domain.Coupon.Ports.Out;

import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.CouponId;

import java.util.List;
import java.util.Optional;

public interface CouponRepositoryPort {
    Coupon save(Coupon coupon);
    Optional<Coupon> findByCode(String code);
    Optional<Coupon> findById(CouponId id);
    List<Coupon> findAll();
}
