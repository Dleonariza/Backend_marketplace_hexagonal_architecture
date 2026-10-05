package com.marketplace.hexagonal.Domain.Product.Ports.In;

import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductType;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public interface UpdateProductUseCase {
    Product updateProduct(ProductId id, SellerProfileId sellerId, CategoryId categoryId,
                          String name, String description, String slug,
                          Money basePrice, ProductType productType);
}
