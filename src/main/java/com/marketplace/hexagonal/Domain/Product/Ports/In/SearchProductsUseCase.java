package com.marketplace.hexagonal.Domain.Product.Ports.In;

import com.marketplace.hexagonal.Domain.Product.Models.PageResult;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductSearchCriteria;

public interface SearchProductsUseCase {
    PageResult<Product> searchProducts(ProductSearchCriteria criteria, int page, int size);
}
