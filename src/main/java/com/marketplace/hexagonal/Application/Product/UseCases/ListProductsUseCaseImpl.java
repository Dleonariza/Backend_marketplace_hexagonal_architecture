package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductStatus;
import com.marketplace.hexagonal.Domain.Product.Ports.In.ListProductsUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

import java.util.List;

public class ListProductsUseCaseImpl extends ProductUseCaseSupport implements ListProductsUseCase {

    public ListProductsUseCaseImpl(ProductRepositoryPort productRepo) { super(productRepo); }

    @Override
    public List<Product> getAllActiveProducts() {
        return productRepo.findAllActive();
    }

    @Override
    public List<Product> getProductsBySeller(SellerProfileId sellerId) {
        return productRepo.findBySellerId(sellerId);
    }

    @Override
    public List<Product> getProductsBySellerAndStatus(SellerProfileId sellerId, ProductStatus status) {
        return productRepo.findBySellerIdAndStatus(sellerId, status);
    }
}
