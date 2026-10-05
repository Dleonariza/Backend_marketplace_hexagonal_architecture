package com.marketplace.hexagonal.Domain.Cart.Ports.Out;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Models.CartId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;
import java.util.Optional;

public interface CartRepositoryPort {
    Cart save(Cart cart);
    Optional<Cart> findById(CartId id);
    Optional<Cart> findActiveByUserId(UserId userId);
    Optional<Cart> findActiveBySessionId(String sessionId);
    List<Cart> findExpiredActive();
}
