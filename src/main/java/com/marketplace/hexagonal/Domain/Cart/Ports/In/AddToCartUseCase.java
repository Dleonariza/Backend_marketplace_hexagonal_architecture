package com.marketplace.hexagonal.Domain.Cart.Ports.In;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface AddToCartUseCase {
    Cart addToCart(UserId userId, String sessionId, String productId, String variantId, int quantity);
}
