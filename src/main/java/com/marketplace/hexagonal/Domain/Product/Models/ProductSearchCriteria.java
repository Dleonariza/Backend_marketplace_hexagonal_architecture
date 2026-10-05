package com.marketplace.hexagonal.Domain.Product.Models;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

import java.math.BigDecimal;

public record ProductSearchCriteria(
        String keyword,
        CategoryId categoryId,
        SellerProfileId sellerId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        ProductType productType
) {}
