package com.marketplace.hexagonal.Infrastructura.DTO.Order;

import com.marketplace.hexagonal.Domain.Order.Models.OrderItem;

import java.math.BigDecimal;
import java.util.Map;

public record OrderItemResponseDTO(
        String id,
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
        BigDecimal itemSubtotal,
        String currency
) {
    public static OrderItemResponseDTO from(OrderItem item) {
        return new OrderItemResponseDTO(
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
                item.getItemSubtotal().amount(),
                item.getUnitPrice().currency()
        );
    }
}
