package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductOwnershipException;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

abstract class ProductUseCaseSupport {

    protected final ProductRepositoryPort productRepo;

    protected ProductUseCaseSupport(ProductRepositoryPort productRepo) {
        this.productRepo = productRepo;
    }

    protected Product loadAndVerifyOwnership(ProductId id, SellerProfileId sellerId) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        if (!product.getSellerId().equals(sellerId))
            throw new ProductOwnershipException();
        return product;
    }
}
