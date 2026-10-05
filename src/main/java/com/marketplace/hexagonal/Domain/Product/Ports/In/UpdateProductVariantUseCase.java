package com.marketplace.hexagonal.Domain.Product.Ports.In;

import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

import java.util.Map;

public interface UpdateProductVariantUseCase {
    Product updateVariant(ProductId id, SellerProfileId sellerId, ProductVariantId variantId,
                          String sku, Map<String, String> attributes, Money price, int stock);
}
