package com.marketplace.hexagonal.Application.Cart.UseCases;

import com.marketplace.hexagonal.Domain.Cart.Models.*;
import com.marketplace.hexagonal.Domain.Cart.Ports.In.MergeCartsUseCase;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.StockReservationRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.Optional;

public class MergeCartsUseCaseImpl implements MergeCartsUseCase {

    private static final int RESERVATION_TTL_MINUTES = 30;

    private final CartRepositoryPort cartRepo;
    private final StockReservationRepositoryPort stockRepo;

    public MergeCartsUseCaseImpl(CartRepositoryPort cartRepo,
                                 StockReservationRepositoryPort stockRepo) {
        this.cartRepo = cartRepo;
        this.stockRepo = stockRepo;
    }

    @Override
    public Cart mergeCarts(UserId userId, String sessionId) {
        Optional<Cart> anonymousOpt = cartRepo.findActiveBySessionId(sessionId);
        Cart userCart = cartRepo.findActiveByUserId(userId)
                .orElseGet(() -> cartRepo.save(Cart.create(userId)));

        if (anonymousOpt.isEmpty()) return userCart;

        Cart anonymousCart = anonymousOpt.get();
        if (anonymousCart.getItems().isEmpty()) {
            anonymousCart.abandon();
            cartRepo.save(anonymousCart);
            return userCart;
        }

        for (CartItem item : anonymousCart.getItems()) {
            userCart.addItem(
                    item.getProductId(),
                    item.getVariantId(),
                    item.getVariantSku(),
                    item.getVariantAttributes(),
                    item.getSellerId(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getProductName(),
                    item.getProductSlug(),
                    item.getPrimaryImageUrl());

            stockRepo.findActiveByCartAndProduct(anonymousCart.getId(),
                    item.getProductId(), item.getVariantId())
                    .ifPresent(r -> {
                        Optional<StockReservation> existing = stockRepo
                                .findActiveByCartAndProduct(userCart.getId(),
                                        item.getProductId(), item.getVariantId());
                        if (existing.isPresent()) {
                            existing.get().updateQuantity(
                                    existing.get().getQuantity() + r.getQuantity());
                            stockRepo.save(existing.get());
                        } else {
                            stockRepo.save(StockReservation.reserve(
                                    userCart.getId(),
                                    item.getProductId(),
                                    item.getVariantId(),
                                    r.getQuantity(),
                                    RESERVATION_TTL_MINUTES));
                        }
                        r.release();
                        stockRepo.save(r);
                    });
        }

        stockRepo.releaseAllByCartId(anonymousCart.getId());
        anonymousCart.abandon();
        cartRepo.save(anonymousCart);

        return cartRepo.save(userCart);
    }
}
