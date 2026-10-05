package com.marketplace.hexagonal.Infrastructura.Mappers;

import com.marketplace.hexagonal.Domain.Product.Models.*;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.*;
import com.marketplace.hexagonal.Infrastructura.Entities.Seller.SellerProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    default ProductVariantEntity toVariantEntity(ProductVariant variant, ProductEntity productEntity) {
        if (variant == null) return null;

        return ProductVariantEntity.builder()
                .id(UUID.fromString(variant.getId().getValue()))
                .product(productEntity)
                .sku(variant.getSku())
                .attributes(variant.getAttributes() != null ? variant.getAttributes() : new java.util.HashMap<>())
                .priceAmount(variant.getPrice() != null ? variant.getPrice().amount() : null)
                .priceCurrency(variant.getPrice() != null ? variant.getPrice().currency() : null)
                .stock(variant.getStock())
                .createdAt(java.time.Instant.now())
                .updatedAt(java.time.Instant.now())
                .build();
    }

    default ProductVariant toVariantDomain(ProductVariantEntity entity) {
        if (entity == null) return null;

        Money price = (entity.getPriceAmount() != null && entity.getPriceCurrency() != null)
                ? Money.of(entity.getPriceAmount(), entity.getPriceCurrency())
                : null;

        return ProductVariant.builder()
                .id(ProductVariantId.of(entity.getId().toString()))
                .sku(entity.getSku())
                .attributes(entity.getAttributes() != null ? entity.getAttributes() : new java.util.HashMap<>())
                .price(price)
                .stock(entity.getStock())
                .active(true)
                .build();
    }

    default ProductImageEntity toImageEntity(ProductImage image, ProductEntity productEntity) {
        if (image == null) return null;

        return ProductImageEntity.builder()
                .id(UUID.fromString(image.getId().getValue()))
                .product(productEntity)
                .url(image.getUrl())
                .altText(image.getAltText())
                .displayOrder(image.getDisplayOrder())
                .primary(image.isPrimary())
                .build();
    }

    default ProductImage toImageDomain(ProductImageEntity entity) {
        if (entity == null) return null;

        return ProductImage.builder()
                .id(ProductImageId.of(entity.getId().toString()))
                .url(entity.getUrl())
                .altText(entity.getAltText())
                .displayOrder(entity.getDisplayOrder())
                .primary(entity.isPrimary())
                .build();
    }

    default ProductEntity toEntity(Product product,
                                   SellerProfileEntity sellerEntity,
                                   CategoryEntity categoryEntity) {
        if (product == null) return null;

        ProductEntity entity = ProductEntity.builder()
                .id(UUID.fromString(product.getId().getValue()))
                .seller(sellerEntity)
                .category(categoryEntity)
                .name(product.getName())
                .description(product.getDescription())
                .slug(product.getSlug())
                .basePriceAmount(product.getBasePrice().amount())
                .basePriceCurrency(product.getBasePrice().currency())
                .productType(product.getProductType())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .variants(new ArrayList<>())
                .images(new ArrayList<>())
                .build();

        if (product.getVariants() != null) {
            List<ProductVariantEntity> variantEntities = product.getVariants().stream()
                    .map(v -> toVariantEntity(v, entity))
                    .collect(Collectors.toList());
            entity.getVariants().addAll(variantEntities);
        }

        if (product.getImages() != null) {
            List<ProductImageEntity> imageEntities = product.getImages().stream()
                    .map(i -> toImageEntity(i, entity))
                    .collect(Collectors.toList());
            entity.getImages().addAll(imageEntities);
        }

        return entity;
    }

    default Product toDomain(ProductEntity entity) {
        if (entity == null) return null;

        List<ProductVariant> variants = entity.getVariants() != null
                ? entity.getVariants().stream().map(this::toVariantDomain).collect(Collectors.toList())
                : new ArrayList<>();

        List<ProductImage> images = entity.getImages() != null
                ? entity.getImages().stream().map(this::toImageDomain).collect(Collectors.toList())
                : new ArrayList<>();

        return Product.reconstitute(
                ProductId.of(entity.getId().toString()),
                SellerProfileId.of(entity.getSeller().getId().toString()),
                CategoryId.of(entity.getCategory().getId().toString()),
                entity.getName(),
                entity.getDescription(),
                entity.getSlug(),
                Money.of(entity.getBasePriceAmount(), entity.getBasePriceCurrency()),
                entity.getProductType(),
                entity.getStatus(),
                variants,
                images,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
