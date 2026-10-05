package com.marketplace.hexagonal.Domain.Product.Ports.In;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public interface DeactivateProductUseCase {
    Product deactivateProduct(ProductId id, SellerProfileId sellerId);
}
