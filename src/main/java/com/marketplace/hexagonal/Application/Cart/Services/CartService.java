package com.marketplace.hexagonal.Application.Cart.Services;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Models.PriceChangeInfo;
import com.marketplace.hexagonal.Domain.Cart.Ports.In.*;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CartService implements
        GetCartUseCase,
        AddToCartUseCase,
        RemoveFromCartUseCase,
        UpdateCartItemQuantityUseCase,
        ClearCartUseCase,
        GetOrCreateAnonymousCartUseCase,
        MergeCartsUseCase,
        CheckPriceChangesUseCase {
    private final GetCartUseCase getCartUseCase;
    private final AddToCartUseCase addToCartUseCase;
    private final RemoveFromCartUseCase removeFromCartUseCase;
    private final UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final GetOrCreateAnonymousCartUseCase getOrCreateAnonymousCartUseCase;
    private final MergeCartsUseCase mergeCartsUseCase;
    private final CheckPriceChangesUseCase checkPriceChangesUseCase;

    @Override
    public Cart getOrCreateCart(UserId userId) {
        return getCartUseCase.getOrCreateCart(userId);
    }

    @Override
    public Cart getOrCreateAnonymousCart(String sessionId) {
        return getOrCreateAnonymousCartUseCase.getOrCreateAnonymousCart(sessionId);
    }

    @Override
    public Cart addToCart(UserId userId, String sessionId, String productId, String variantId, int quantity) {
        return addToCartUseCase.addToCart(userId, sessionId, productId, variantId, quantity);
    }

    @Override
    public Cart removeFromCart(UserId userId, String sessionId, String cartItemId) {
        return removeFromCartUseCase.removeFromCart(userId, sessionId, cartItemId);
    }

    @Override
    public Cart updateQuantity(UserId userId, String sessionId, String cartItemId, int newQuantity) {
        return updateCartItemQuantityUseCase.updateQuantity(userId, sessionId, cartItemId, newQuantity);
    }

    @Override
    public Cart clearCart(UserId userId, String sessionId) {
        return clearCartUseCase.clearCart(userId, sessionId);
    }

    @Override
    public Cart mergeCarts(UserId userId, String sessionId) {
        return mergeCartsUseCase.mergeCarts(userId, sessionId);
    }

    @Override
    public List<PriceChangeInfo> checkPriceChanges(Cart cart) {
        return checkPriceChangesUseCase.checkPriceChanges(cart);
    }
}
