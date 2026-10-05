package com.marketplace.hexagonal.Domain.Product.Ports.In;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;

public interface GetProductUseCase {
    Product getProductById(ProductId id);
}
