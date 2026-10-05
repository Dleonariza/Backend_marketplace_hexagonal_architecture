package com.marketplace.hexagonal.Infrastructura.DTO.Favorite;

import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductImage;

import java.math.BigDecimal;
import java.time.Instant;

public record FavoriteResponseDTO(
        String id,
        String productId,
        String productName,
        String productSlug,
        String primaryImageUrl,
        BigDecimal price,
        String currency,
        Instant createdAt
) {
    public static FavoriteResponseDTO from(Favorite favorite) {
        Product p = favorite.getProduct();

        String imageUrl = null;
        BigDecimal price = null;
        String currency = null;
        String name = null;
        String slug = null;

        if (p != null) {
            imageUrl = p.getImages().stream()
                    .filter(ProductImage::isPrimary)
                    .map(ProductImage::getUrl)
                    .findFirst()
                    .orElse(p.getImages().isEmpty() ? null : p.getImages().get(0).getUrl());

            if (p.getBasePrice() != null) {
                price = p.getBasePrice().amount();
                currency = p.getBasePrice().currency();
            }
            name = p.getName();
            slug = p.getSlug();
        }

        return new FavoriteResponseDTO(
                favorite.getFavoriteId().getValue(),
                favorite.getProductId().getValue(),
                name,
                slug,
                imageUrl,
                price,
                currency,
                favorite.getCreatedAt()
        );
    }
}
