package com.marketplace.hexagonal.Application.Cart.UseCases;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Ports.In.GetCartUseCase;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public class GetCartUseCaseImpl implements GetCartUseCase {

    private final CartRepositoryPort cartRepo;

    public GetCartUseCaseImpl(CartRepositoryPort cartRepo) {
        this.cartRepo = cartRepo;
    }

    @Override
    public Cart getOrCreateCart(UserId userId) {
        return cartRepo.findActiveByUserId(userId)
                .orElseGet(() -> cartRepo.save(Cart.create(userId)));
    }
}
