package com.marketplace.hexagonal.Domain.Product.Models;

import com.marketplace.hexagonal.Domain.Product.Exceptions.DuplicateSkuException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.InvalidProductStatusTransitionException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductImageNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductVariantNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Product {

    private ProductId id;
    private SellerProfileId sellerId;
    private CategoryId categoryId;

    private String name;
    private String description;
    private String slug;
    private Money basePrice;

    private ProductType productType;
    private ProductStatus status;

    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    public static Product create(
            SellerProfileId sellerId,
            CategoryId categoryId,
            String name,
            String description,
            String slug,
            Money basePrice,
            ProductType productType) {
        Instant now = Instant.now();
        return Product.builder()
                .id(ProductId.generate())
                .sellerId(sellerId)
                .categoryId(categoryId)
                .name(name)
                .description(description)
                .slug(slug)
                .basePrice(basePrice)
                .productType(productType)
                .status(ProductStatus.DRAFT)
                .variants(new ArrayList<>())
                .images(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static Product reconstitute(
            ProductId id,
            SellerProfileId sellerId,
            CategoryId categoryId,
            String name,
            String description,
            String slug,
            Money basePrice,
            ProductType productType,
            ProductStatus status,
            List<ProductVariant> variants,
            List<ProductImage> images,
            Instant createdAt,
            Instant updatedAt) {
        Product p = new Product();
        p.id = id;
        p.sellerId = sellerId;
        p.categoryId = categoryId;
        p.name = name;
        p.description = description;
        p.slug = slug;
        p.basePrice = basePrice;
        p.productType = productType;
        p.status = status;
        p.variants = variants != null ? new ArrayList<>(variants) : new ArrayList<>();
        p.images = images != null ? new ArrayList<>(images) : new ArrayList<>();
        p.createdAt = createdAt;
        p.updatedAt = updatedAt;
        return p;
    }

    public void updateInfo(
            CategoryId categoryId,
            String name,
            String description,
            String slug,
            Money basePrice,
            ProductType productType) {
        if (this.status == ProductStatus.DELETED)
            throw new InvalidProductStatusTransitionException("Cannot update a deleted product.");

        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.basePrice = basePrice;
        this.productType = productType;
        this.updatedAt = Instant.now();
    }

    public void publish() {
        if (this.status != ProductStatus.DRAFT)
            throw new InvalidProductStatusTransitionException(this.status, ProductStatus.ACTIVE);

        if (this.productType == ProductType.PHYSICAL && this.images.isEmpty())
            throw new InvalidProductStatusTransitionException(
                    "Physical products must have at least one image before publishing.");

        this.status = ProductStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        if (this.status != ProductStatus.ACTIVE)
            throw new InvalidProductStatusTransitionException(this.status, ProductStatus.INACTIVE);
        this.status = ProductStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void reactivate() {
        if (this.status != ProductStatus.INACTIVE)
            throw new InvalidProductStatusTransitionException(this.status, ProductStatus.ACTIVE);
        this.status = ProductStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void delete() {
        if (this.status == ProductStatus.DELETED)
            throw new InvalidProductStatusTransitionException("Product is already deleted.");
        this.status = ProductStatus.DELETED;
        this.updatedAt = Instant.now();
    }

    public ProductVariant addVariant(
            String sku,
            Map<String, String> attributes,
            Money price,
            int stock) {
        if (this.status == ProductStatus.DELETED)
            throw new InvalidProductStatusTransitionException("Cannot modify a deleted product.");

        boolean skuExists = variants.stream()
                .anyMatch(v -> v.getSku().equalsIgnoreCase(sku));
        if (skuExists)
            throw new DuplicateSkuException(sku);

        ProductVariant variant = ProductVariant.create(sku, attributes, price, stock);
        this.variants.add(variant);
        this.updatedAt = Instant.now();
        return variant;
    }

    public void updateVariant(
            ProductVariantId variantId,
            String sku,
            Map<String, String> attributes,
            Money price,
            int stock) {
        if (this.status == ProductStatus.DELETED)
            throw new InvalidProductStatusTransitionException("Cannot modify a deleted product.");

        ProductVariant target = findVariant(variantId);

        boolean skuTaken = variants.stream()
                .anyMatch(v -> v.getSku().equalsIgnoreCase(sku)
                        && !v.getId().equals(variantId));
        if (skuTaken) throw new DuplicateSkuException(sku);

        target.update(sku, attributes, price, stock);
        this.updatedAt = Instant.now();
    }

    public void removeVariant(ProductVariantId variantId) {
        if (this.status == ProductStatus.DELETED)
            throw new InvalidProductStatusTransitionException("Cannot modify a deleted product.");

        ProductVariant target = findVariant(variantId);
        this.variants.remove(target);
        this.updatedAt = Instant.now();
    }

    public ProductImage addImage(String url, String altText) {
        if (this.status == ProductStatus.DELETED)
            throw new InvalidProductStatusTransitionException("Cannot modify a deleted product.");

        ProductImage image = ProductImage.create(url, altText);
        image.setDisplayOrder(this.images.size() + 1);
        if (this.images.isEmpty()) image.setPrimary(true);
        this.images.add(image);
        this.updatedAt = Instant.now();
        return image;
    }

    public void removeImage(ProductImageId imageId) {
        if (this.status == ProductStatus.DELETED)
            throw new InvalidProductStatusTransitionException("Cannot modify a deleted product.");

        ProductImage target = findImage(imageId);
        this.images.remove(target);

        if (target.isPrimary() && !this.images.isEmpty())
            this.images.get(0).setPrimary(true);

        for (int i = 0; i < this.images.size(); i++)
            this.images.get(i).setDisplayOrder(i + 1);

        this.updatedAt = Instant.now();
    }

    public void setPrimaryImage(ProductImageId imageId) {
        if (this.status == ProductStatus.DELETED)
            throw new InvalidProductStatusTransitionException("Cannot modify a deleted product.");

        findImage(imageId);
        this.images.forEach(img -> img.setPrimary(img.getId().equals(imageId)));
        this.updatedAt = Instant.now();
    }

    private ProductVariant findVariant(ProductVariantId id) {
        return variants.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProductVariantNotFoundException(id));
    }

    private ProductImage findImage(ProductImageId id) {
        return images.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProductImageNotFoundException(id));
    }
}
