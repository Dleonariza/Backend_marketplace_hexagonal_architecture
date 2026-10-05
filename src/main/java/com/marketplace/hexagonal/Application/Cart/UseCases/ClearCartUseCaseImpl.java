package com.marketplace.hexagonal.Application.Cart.UseCases;

import com.marketplace.hexagonal.Domain.Cart.Exceptions.CartNotFoundException;
import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Ports.In.ClearCartUseCase;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.StockReservationRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public class ClearCartUseCaseImpl implements ClearCartUseCase {

    private final CartRepositoryPort cartRepo;
    private final StockReservationRepositoryPort stockRepo;

    public ClearCartUseCaseImpl(CartRepositoryPort cartRepo,
                                StockReservationRepositoryPort stockRepo) {
        this.cartRepo = cartRepo;
        this.stockRepo = stockRepo;
    }

    @Override
    public Cart clearCart(UserId userId, String sessionId) {
        Cart cart = userId != null
                ? cartRepo.findActiveByUserId(userId)
                        .orElseThrow(() -> new CartNotFoundException("No active cart found for user"))
                : cartRepo.findActiveBySessionId(sessionId)
                        .orElseThrow(() -> new CartNotFoundException("No active cart found for session"));

        stockRepo.releaseAllByCartId(cart.getId());
        cart.clear();
        return cartRepo.save(cart);
    }
}
