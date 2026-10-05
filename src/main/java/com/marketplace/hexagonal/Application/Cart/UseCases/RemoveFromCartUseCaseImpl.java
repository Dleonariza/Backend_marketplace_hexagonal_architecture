package com.marketplace.hexagonal.Application.Cart.UseCases;

import com.marketplace.hexagonal.Domain.Cart.Exceptions.CartNotFoundException;
import com.marketplace.hexagonal.Domain.Cart.Models.*;
import com.marketplace.hexagonal.Domain.Cart.Ports.In.RemoveFromCartUseCase;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.StockReservationRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public class RemoveFromCartUseCaseImpl implements RemoveFromCartUseCase {

    private final CartRepositoryPort cartRepo;
    private final StockReservationRepositoryPort stockRepo;

    public RemoveFromCartUseCaseImpl(CartRepositoryPort cartRepo,
                                     StockReservationRepositoryPort stockRepo) {
        this.cartRepo = cartRepo;
        this.stockRepo = stockRepo;
    }

    @Override
    public Cart removeFromCart(UserId userId, String sessionId, String cartItemIdStr) {
        Cart cart = requireActiveCart(userId, sessionId);
        CartItemId itemId = CartItemId.of(cartItemIdStr);

        CartItem item = cart.findItem(itemId)
                .orElseThrow(() -> new com.marketplace.hexagonal.Domain.Cart.Exceptions.CartItemNotFoundException(cartItemIdStr));

        stockRepo.findActiveByCartAndProduct(cart.getId(), item.getProductId(), item.getVariantId())
                .ifPresent(r -> {
                    r.release();
                    stockRepo.save(r);
                });

        cart.removeItem(itemId);
        return cartRepo.save(cart);
    }

    private Cart requireActiveCart(UserId userId, String sessionId) {
        if (userId != null) {
            return cartRepo.findActiveByUserId(userId)
                    .orElseThrow(() -> new CartNotFoundException("No active cart found for user"));
        }
        return cartRepo.findActiveBySessionId(sessionId)
                .orElseThrow(() -> new CartNotFoundException("No active cart found for session"));
    }
}
