package com.marketplace.hexagonal.Domain.Cart.Models;

import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import lombok.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartItem {

    private CartItemId id;
    private ProductId productId;

    private ProductVariantId variantId;
    private SellerProfileId sellerId;
    private int quantity;

    private Money unitPrice;

    private String productName;
    private String productSlug;
    private String primaryImageUrl;

    private String variantSku;

    private Map<String, String> variantAttributes;

    private Instant addedAt;

    static CartItem create(
            ProductId productId,
            ProductVariantId variantId,
            String variantSku,
            Map<String, String> variantAttributes,
            SellerProfileId sellerId,
            int quantity,
            Money unitPrice,
            String productName,
            String productSlug,
            String primaryImageUrl) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Cart item quantity must be positive");

        return CartItem.builder()
                .id(CartItemId.generate())
                .productId(productId)
                .variantId(variantId)
                .variantSku(variantSku)
                .variantAttributes(variantAttributes != null ? new HashMap<>(variantAttributes) : null)
                .sellerId(sellerId)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .productName(productName)
                .productSlug(productSlug)
                .primaryImageUrl(primaryImageUrl)
                .addedAt(Instant.now())
                .build();
    }

    void updateQuantity(int newQuantity) {
        if (newQuantity <= 0)
            throw new IllegalArgumentException("Cart item quantity must be positive");
        this.quantity = newQuantity;
    }

    public boolean matchesProduct(ProductId productId, ProductVariantId variantId) {
        boolean sameProduct = this.productId.equals(productId);
        boolean sameVariant = (this.variantId == null && variantId == null)
                || (this.variantId != null && this.variantId.equals(variantId));
        return sameProduct && sameVariant;
    }
}
