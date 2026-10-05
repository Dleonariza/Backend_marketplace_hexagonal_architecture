package com.marketplace.hexagonal.Domain.Product.Events;

import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public record LowStockAlertEvent(
        ProductId productId,
        String productName,
        ProductVariantId variantId,
        String sku,
        int currentStock,
        SellerProfileId sellerProfileId
) {
    public static final int LOW_STOCK_THRESHOLD = 5;
}
