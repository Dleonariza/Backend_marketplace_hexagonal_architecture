package com.marketplace.hexagonal.Application.Coupon.UseCases;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Coupon.Exceptions.CouponNotFoundException;
import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Ports.In.ApplyCouponUseCase;
import com.marketplace.hexagonal.Domain.Coupon.Ports.Out.CouponRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApplyCouponUseCaseImpl implements ApplyCouponUseCase {

    private final CartRepositoryPort cartRepo;
    private final CouponRepositoryPort couponRepo;

    @Override
    public Cart applyCoupon(UserId userId, String sessionId, String couponCode) {
        Cart cart = findActiveCart(userId, sessionId);

        Coupon coupon = couponRepo.findByCode(couponCode.toUpperCase().trim())
                .orElseThrow(() -> new CouponNotFoundException(couponCode));

        cart.applyCoupon(coupon);
        return cartRepo.save(cart);
    }

    private Cart findActiveCart(UserId userId, String sessionId) {
        if (userId != null) {
            return cartRepo.findActiveByUserId(userId)
                    .orElseGet(() -> cartRepo.save(Cart.create(userId)));
        }
        return cartRepo.findActiveBySessionId(sessionId)
                .orElseGet(() -> cartRepo.save(Cart.createAnonymous(sessionId)));
    }
}
