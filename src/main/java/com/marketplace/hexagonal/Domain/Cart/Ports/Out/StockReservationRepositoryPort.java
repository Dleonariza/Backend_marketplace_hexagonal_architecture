package com.marketplace.hexagonal.Domain.Cart.Ports.Out;

import com.marketplace.hexagonal.Domain.Cart.Models.CartId;
import com.marketplace.hexagonal.Domain.Cart.Models.StockReservation;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;

import java.util.Optional;

public interface StockReservationRepositoryPort {
    StockReservation save(StockReservation reservation);
    Optional<StockReservation> findActiveByCartAndProduct(CartId cartId, ProductId productId, ProductVariantId variantId);

    int sumActiveReservedQuantity(ProductId productId, ProductVariantId variantId);
    void releaseAllByCartId(CartId cartId);
}
