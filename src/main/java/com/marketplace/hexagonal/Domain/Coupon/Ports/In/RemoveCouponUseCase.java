package com.marketplace.hexagonal.Domain.Coupon.Ports.In;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface RemoveCouponUseCase {
    Cart removeCoupon(UserId userId, String sessionId);
}
