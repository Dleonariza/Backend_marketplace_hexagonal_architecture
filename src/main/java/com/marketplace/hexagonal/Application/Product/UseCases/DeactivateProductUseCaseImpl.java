package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.DeactivateProductUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public class DeactivateProductUseCaseImpl extends ProductUseCaseSupport implements DeactivateProductUseCase {

    public DeactivateProductUseCaseImpl(ProductRepositoryPort productRepo) { super(productRepo); }

    @Override
    public Product deactivateProduct(ProductId id, SellerProfileId sellerId) {
        Product product = loadAndVerifyOwnership(id, sellerId);
        product.deactivate();
        return productRepo.save(product);
    }
}
