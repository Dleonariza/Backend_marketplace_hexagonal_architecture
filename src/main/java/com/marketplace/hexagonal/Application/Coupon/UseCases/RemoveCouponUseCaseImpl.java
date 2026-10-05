package com.marketplace.hexagonal.Application.Coupon.UseCases;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Coupon.Ports.In.RemoveCouponUseCase;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RemoveCouponUseCaseImpl implements RemoveCouponUseCase {

    private final CartRepositoryPort cartRepo;

    @Override
    public Cart removeCoupon(UserId userId, String sessionId) {
        Cart cart = findActiveCart(userId, sessionId);
        cart.removeCoupon();
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
