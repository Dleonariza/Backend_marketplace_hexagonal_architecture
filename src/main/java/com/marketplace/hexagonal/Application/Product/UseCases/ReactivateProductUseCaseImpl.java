package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.ReactivateProductUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public class ReactivateProductUseCaseImpl extends ProductUseCaseSupport implements ReactivateProductUseCase {

    public ReactivateProductUseCaseImpl(ProductRepositoryPort productRepo) { super(productRepo); }

    @Override
    public Product reactivateProduct(ProductId id, SellerProfileId sellerId) {
        Product product = loadAndVerifyOwnership(id, sellerId);
        product.reactivate();
        return productRepo.save(product);
    }
}
