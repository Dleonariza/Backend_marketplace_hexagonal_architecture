package com.marketplace.hexagonal.Domain.Product.Ports.Out;

import com.marketplace.hexagonal.Domain.Product.Models.PageResult;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductSearchCriteria;
import com.marketplace.hexagonal.Domain.Product.Models.ProductStatus;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(ProductId id);
    List<Product> findBySellerId(SellerProfileId sellerId);
    List<Product> findBySellerIdAndStatus(SellerProfileId sellerId, ProductStatus status);
    List<Product> findByStatus(ProductStatus status);
    List<Product> findAllActive();
    boolean existsBySlug(String slug);
    PageResult<Product> search(ProductSearchCriteria criteria, int page, int size);
}
