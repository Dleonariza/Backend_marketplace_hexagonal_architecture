package com.marketplace.hexagonal.Domain.Cart.Models;

import com.marketplace.hexagonal.Domain.Product.Models.Money;

public record PriceChangeInfo(
        CartItemId cartItemId,
        Money snapshotPrice,
        Money currentPrice,
        boolean changed
) {
    public static PriceChangeInfo unchanged(CartItemId id, Money price) {
        return new PriceChangeInfo(id, price, price, false);
    }

    public static PriceChangeInfo changed(CartItemId id, Money snapshot, Money current) {
        return new PriceChangeInfo(id, snapshot, current, true);
    }

    public static PriceChangeInfo productGone(CartItemId id, Money snapshot) {
        return new PriceChangeInfo(id, snapshot, null, true);
    }
}
