package com.marketplace.hexagonal.Domain.Cart.Models;

import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StockReservation {

    private StockReservationId id;
    private CartId cartId;
    private ProductId productId;

    private ProductVariantId variantId;
    private int quantity;
    private StockReservationStatus status;
    private Instant expiresAt;
    private Instant createdAt;

    public static StockReservation reserve(
            CartId cartId,
            ProductId productId,
            ProductVariantId variantId,
            int quantity,
            int ttlMinutes) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Reserved quantity must be positive");

        Instant now = Instant.now();
        return StockReservation.builder()
                .id(StockReservationId.generate())
                .cartId(cartId)
                .productId(productId)
                .variantId(variantId)
                .quantity(quantity)
                .status(StockReservationStatus.RESERVED)
                .expiresAt(now.plusSeconds((long) ttlMinutes * 60))
                .createdAt(now)
                .build();
    }

    public static StockReservation reconstitute(
            StockReservationId id,
            CartId cartId,
            ProductId productId,
            ProductVariantId variantId,
            int quantity,
            StockReservationStatus status,
            Instant expiresAt,
            Instant createdAt) {
        StockReservation r = new StockReservation();
        r.id = id;
        r.cartId = cartId;
        r.productId = productId;
        r.variantId = variantId;
        r.quantity = quantity;
        r.status = status;
        r.expiresAt = expiresAt;
        r.createdAt = createdAt;
        return r;
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0)
            throw new IllegalArgumentException("Reserved quantity must be positive");
        this.quantity = newQuantity;
    }

    public void release() {
        this.status = StockReservationStatus.RELEASED;
    }

    public void confirm() {
        this.status = StockReservationStatus.CONFIRMED;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean isActive() {
        return this.status == StockReservationStatus.RESERVED && !isExpired();
    }
}
