package com.marketplace.hexagonal.Infrastructura.DTO.Product;

import com.marketplace.hexagonal.Domain.Product.Models.ProductImage;

public record ProductImageResponseDTO(
        String id,
        String url,
        String altText,
        int displayOrder,
        boolean primary
) {
    public static ProductImageResponseDTO from(ProductImage image) {
        return new ProductImageResponseDTO(
                image.getId().getValue(),
                image.getUrl(),
                image.getAltText(),
                image.getDisplayOrder(),
                image.isPrimary()
        );
    }
}
