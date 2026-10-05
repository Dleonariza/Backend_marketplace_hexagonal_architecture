package com.marketplace.hexagonal.Domain.Cart.Ports.In;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface GetCartUseCase {

    Cart getOrCreateCart(UserId userId);
}
