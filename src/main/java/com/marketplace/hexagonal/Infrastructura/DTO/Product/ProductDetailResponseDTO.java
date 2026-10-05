package com.marketplace.hexagonal.Infrastructura.DTO.Product;

import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record ProductDetailResponseDTO(

        String id,
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
        Instant updatedAt,

        String categoryId,
        String categoryName,
        String categorySlug,

        String sellerId,
        String sellerStoreName,
        String sellerLogoUrl,
        String sellerContactEmail,

        List<ProductSummaryResponseDTO> relatedProducts
) {
    public static ProductDetailResponseDTO from(Product product,
                                                Category category,
                                                SellerProfile seller,
                                                List<Product> related) {
        List<ProductVariantResponseDTO> variantDTOs = product.getVariants().stream()
                .map(ProductVariantResponseDTO::from)
                .collect(Collectors.toList());

        List<ProductImageResponseDTO> imageDTOs = product.getImages().stream()
                .map(ProductImageResponseDTO::from)
                .collect(Collectors.toList());

        List<ProductSummaryResponseDTO> relatedDTOs = related.stream()
                .map(ProductSummaryResponseDTO::from)
                .collect(Collectors.toList());

        return new ProductDetailResponseDTO(
                product.getId().getValue(),
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
                product.getUpdatedAt(),
                category.getId().getValue(),
                category.getName(),
                category.getSlug(),
                seller.getId().getValue(),
                seller.getStoreName(),
                seller.getLogoUrl(),
                seller.getContactEmail(),
                relatedDTOs
        );
    }
}
