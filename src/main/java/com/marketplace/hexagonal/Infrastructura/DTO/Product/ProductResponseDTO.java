package com.marketplace.hexagonal.Infrastructura.DTO.Product;

import com.marketplace.hexagonal.Domain.Product.Models.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record ProductResponseDTO(
        String id,
        String sellerId,
        String categoryId,
        String productType,
        String name,
        String description,
        String slug,
        BigDecimal basePriceAmount,
        String basePriceCurrency,
        String status,
        List<ProductVariantResponseDTO> variants,
        List<ProductImageResponseDTO> images,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProductResponseDTO from(Product product) {
        List<ProductVariantResponseDTO> variantDTOs = product.getVariants().stream()
                .map(ProductVariantResponseDTO::from)
                .collect(Collectors.toList());

        List<ProductImageResponseDTO> imageDTOs = product.getImages().stream()
                .map(ProductImageResponseDTO::from)
                .collect(Collectors.toList());

        return new ProductResponseDTO(
                product.getId().getValue(),
                product.getSellerId().getValue(),
                product.getCategoryId().getValue(),
                product.getProductType().name(),
                product.getName(),
                product.getDescription(),
                product.getSlug(),
                product.getBasePrice().amount(),
                product.getBasePrice().currency(),
                product.getStatus().name(),
                variantDTOs,
                imageDTOs,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
