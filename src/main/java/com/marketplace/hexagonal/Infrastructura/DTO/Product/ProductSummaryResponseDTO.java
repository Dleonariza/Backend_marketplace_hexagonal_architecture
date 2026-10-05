package com.marketplace.hexagonal.Infrastructura.DTO.Product;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductImage;

import java.math.BigDecimal;

public record ProductSummaryResponseDTO(
        String id,
        String name,
        String slug,
        String productType,
        String status,
        BigDecimal basePriceAmount,
        String basePriceCurrency,
        String primaryImageUrl
) {
    public static ProductSummaryResponseDTO from(Product product) {
        String primaryImageUrl = product.getImages().stream()
                .filter(ProductImage::isPrimary)
                .map(ProductImage::getUrl)
                .findFirst()
                .orElse(null);

        return new ProductSummaryResponseDTO(
                product.getId().getValue(),
                product.getName(),
                product.getSlug(),
                product.getProductType().name(),
                product.getStatus().name(),
                product.getBasePrice().amount(),
                product.getBasePrice().currency(),
                primaryImageUrl
        );
    }
}
