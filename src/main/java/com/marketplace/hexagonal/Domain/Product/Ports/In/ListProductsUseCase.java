package com.marketplace.hexagonal.Domain.Product.Ports.In;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductStatus;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

import java.util.List;

public interface ListProductsUseCase {
    List<Product> getAllActiveProducts();
    List<Product> getProductsBySeller(SellerProfileId sellerId);
    List<Product> getProductsBySellerAndStatus(SellerProfileId sellerId, ProductStatus status);
}
