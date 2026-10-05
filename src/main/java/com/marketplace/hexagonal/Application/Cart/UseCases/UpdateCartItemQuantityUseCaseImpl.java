package com.marketplace.hexagonal.Application.Cart.UseCases;

import com.marketplace.hexagonal.Domain.Cart.Exceptions.CartItemNotFoundException;
import com.marketplace.hexagonal.Domain.Cart.Exceptions.CartNotFoundException;
import com.marketplace.hexagonal.Domain.Cart.Exceptions.InsufficientStockException;
import com.marketplace.hexagonal.Domain.Cart.Models.*;
import com.marketplace.hexagonal.Domain.Cart.Ports.In.UpdateCartItemQuantityUseCase;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.StockReservationRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariant;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public class UpdateCartItemQuantityUseCaseImpl implements UpdateCartItemQuantityUseCase {

    private final CartRepositoryPort cartRepo;
    private final StockReservationRepositoryPort stockRepo;
    private final ProductRepositoryPort productRepo;

    public UpdateCartItemQuantityUseCaseImpl(CartRepositoryPort cartRepo,
                                             StockReservationRepositoryPort stockRepo,
                                             ProductRepositoryPort productRepo) {
        this.cartRepo = cartRepo;
        this.stockRepo = stockRepo;
        this.productRepo = productRepo;
    }

    @Override
    public Cart updateQuantity(UserId userId, String sessionId, String cartItemIdStr, int newQuantity) {
        if (newQuantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");

        Cart cart = userId != null
                ? cartRepo.findActiveByUserId(userId)
                        .orElseThrow(() -> new CartNotFoundException("No active cart found for user"))
                : cartRepo.findActiveBySessionId(sessionId)
                        .orElseThrow(() -> new CartNotFoundException("No active cart found for session"));
        CartItemId itemId = CartItemId.of(cartItemIdStr);

        CartItem item = cart.findItem(itemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemIdStr));

        productRepo.findById(item.getProductId()).ifPresent(product -> {
            int stock = item.getVariantId() != null
                    ? product.getVariants().stream()
                        .filter(v -> v.getId().equals(item.getVariantId()))
                        .findFirst().map(ProductVariant::getStock).orElse(0)
                    : product.getVariants().stream().mapToInt(ProductVariant::getStock).sum();

            int globalReserved = stockRepo.sumActiveReservedQuantity(
                    item.getProductId(), item.getVariantId());
            int available = stock - (globalReserved - item.getQuantity());

            if (available < newQuantity)
                throw new InsufficientStockException(
                        "Only " + available + " unit(s) available");
        });

        cart.updateItemQuantity(itemId, newQuantity);
        cart = cartRepo.save(cart);

        stockRepo.findActiveByCartAndProduct(cart.getId(), item.getProductId(), item.getVariantId())
                .ifPresent(r -> {
                    r.updateQuantity(newQuantity);
                    stockRepo.save(r);
                });

        return cart;
    }
}
