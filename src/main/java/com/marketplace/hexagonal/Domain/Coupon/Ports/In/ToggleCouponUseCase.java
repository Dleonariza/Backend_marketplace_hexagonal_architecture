package com.marketplace.hexagonal.Domain.Coupon.Ports.In;

public interface ToggleCouponUseCase {
    void activateCoupon(String code);
    void deactivateCoupon(String code);
}
