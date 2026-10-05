package com.marketplace.hexagonal.Domain.Product.Models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductImage {

    private ProductImageId id;
    private String url;
    private String altText;
    private int displayOrder;
    private boolean primary;

    public static ProductImage create(String url, String altText) {
        if (url == null || url.isBlank())
            throw new IllegalArgumentException("Image URL cannot be blank");

        return ProductImage.builder()
                .id(ProductImageId.generate())
                .url(url.trim())
                .altText(altText)
                .displayOrder(0)
                .primary(false)
                .build();
    }
}
