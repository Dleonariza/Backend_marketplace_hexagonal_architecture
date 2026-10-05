package com.marketplace.hexagonal.Application.Cart.UseCases;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Ports.In.GetOrCreateAnonymousCartUseCase;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;

public class GetOrCreateAnonymousCartUseCaseImpl implements GetOrCreateAnonymousCartUseCase {

    private final CartRepositoryPort cartRepo;

    public GetOrCreateAnonymousCartUseCaseImpl(CartRepositoryPort cartRepo) {
        this.cartRepo = cartRepo;
    }

    @Override
    public Cart getOrCreateAnonymousCart(String sessionId) {
        return cartRepo.findActiveBySessionId(sessionId)
                .orElseGet(() -> cartRepo.save(Cart.createAnonymous(sessionId)));
    }
}
