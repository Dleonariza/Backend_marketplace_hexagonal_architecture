package com.marketplace.hexagonal.Domain.Product.Ports.In;

import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public interface DeleteProductUseCase {
    void deleteProduct(ProductId id, SellerProfileId sellerId);
}
