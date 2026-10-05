package com.marketplace.hexagonal.Application.Cart.UseCases;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.StockReservationRepositoryPort;

import java.util.List;

public class ExpireCartsUseCaseImpl {

    private final CartRepositoryPort cartRepo;
    private final StockReservationRepositoryPort stockRepo;

    public ExpireCartsUseCaseImpl(CartRepositoryPort cartRepo,
                                  StockReservationRepositoryPort stockRepo) {
        this.cartRepo = cartRepo;
        this.stockRepo = stockRepo;
    }

    public int expireCarts() {
        List<Cart> expired = cartRepo.findExpiredActive();
        expired.forEach(cart -> {
            stockRepo.releaseAllByCartId(cart.getId());
            cart.abandon();
            cartRepo.save(cart);
        });
        return expired.size();
    }
}
