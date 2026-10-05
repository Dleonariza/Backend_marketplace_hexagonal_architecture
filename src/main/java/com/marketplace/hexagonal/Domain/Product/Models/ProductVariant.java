package com.marketplace.hexagonal.Domain.Product.Models;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductVariant {

    private ProductVariantId id;
    private String sku;

    private Map<String, String> attributes;

    private Money price;
    private int stock;
    private boolean active;

    public static ProductVariant create(
            String sku,
            Map<String, String> attributes,
            Money price,
            int stock) {
        if (sku == null || sku.isBlank())
            throw new IllegalArgumentException("Variant SKU cannot be blank");
        if (stock < 0)
            throw new IllegalArgumentException("Variant stock cannot be negative");

        return ProductVariant.builder()
                .id(ProductVariantId.generate())
                .sku(sku.trim())
                .attributes(attributes != null ? new HashMap<>(attributes) : new HashMap<>())
                .price(price)
                .stock(stock)
                .active(true)
                .build();
    }

    public void update(String sku, Map<String, String> attributes, Money price, int stock) {
        if (sku == null || sku.isBlank())
            throw new IllegalArgumentException("Variant SKU cannot be blank");
        if (stock < 0)
            throw new IllegalArgumentException("Variant stock cannot be negative");

        this.sku = sku.trim();
        this.attributes = attributes != null ? new HashMap<>(attributes) : new HashMap<>();
        this.price = price;
        this.stock = stock;
    }
}
