package com.marketplace.hexagonal.Infrastructura.DTO.Product;

import com.marketplace.hexagonal.Domain.Product.Models.ProductVariant;

import java.math.BigDecimal;
import java.util.Map;

public record ProductVariantResponseDTO(
        String id,
        String sku,
        Map<String, String> attributes,
        BigDecimal priceAmount,
        String priceCurrency,
        int stock,
        boolean active
) {
    public static ProductVariantResponseDTO from(ProductVariant variant) {
        return new ProductVariantResponseDTO(
                variant.getId().getValue(),
                variant.getSku(),
                variant.getAttributes(),
                variant.getPrice() != null ? variant.getPrice().amount() : null,
                variant.getPrice() != null ? variant.getPrice().currency() : null,
                variant.getStock(),
                variant.isActive()
        );
    }
}
