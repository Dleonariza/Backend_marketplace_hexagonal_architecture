package com.marketplace.hexagonal.Infrastructura.DTO.Coupon;

import jakarta.validation.constraints.NotBlank;

public record ApplyCouponRequestDTO(
        @NotBlank String couponCode
) {}
