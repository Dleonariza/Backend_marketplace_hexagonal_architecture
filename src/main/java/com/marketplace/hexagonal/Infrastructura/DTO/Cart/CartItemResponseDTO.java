package com.marketplace.hexagonal.Infrastructura.DTO.Cart;

import com.marketplace.hexagonal.Domain.Cart.Models.CartItem;
import com.marketplace.hexagonal.Domain.Cart.Models.PriceChangeInfo;

import java.math.BigDecimal;
import java.util.Map;

public record CartItemResponseDTO(
        String cartItemId,
        String productId,
        String variantId,
        String sellerId,
        String productName,
        String productSlug,
        String primaryImageUrl,

        String variantSku,
        Map<String, String> variantAttributes,

        int quantity,
        BigDecimal unitPrice,
        String currency,
        BigDecimal subtotal,

        boolean priceChanged,
        BigDecimal currentPrice
) {
    public static CartItemResponseDTO from(CartItem item, PriceChangeInfo priceChange) {
        BigDecimal subtotal = item.getUnitPrice().amount()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        boolean changed = priceChange != null && priceChange.changed();
        BigDecimal currentPrice = (priceChange != null && priceChange.currentPrice() != null)
                ? priceChange.currentPrice().amount() : null;

        return new CartItemResponseDTO(
                item.getId().getValue(),
                item.getProductId().getValue(),
                item.getVariantId() != null ? item.getVariantId().getValue() : null,
                item.getSellerId().getValue(),
                item.getProductName(),
                item.getProductSlug(),
                item.getPrimaryImageUrl(),
                item.getVariantSku(),
                item.getVariantAttributes(),
                item.getQuantity(),
                item.getUnitPrice().amount(),
                item.getUnitPrice().currency(),
                subtotal,
                changed,
                currentPrice);
    }

    public static CartItemResponseDTO from(CartItem item) {
        return from(item, null);
    }
}
