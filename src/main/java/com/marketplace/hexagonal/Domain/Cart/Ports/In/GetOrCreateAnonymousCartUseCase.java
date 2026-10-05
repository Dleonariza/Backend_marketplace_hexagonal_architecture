package com.marketplace.hexagonal.Domain.Cart.Ports.In;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;

public interface GetOrCreateAnonymousCartUseCase {
    Cart getOrCreateAnonymousCart(String sessionId);
}
