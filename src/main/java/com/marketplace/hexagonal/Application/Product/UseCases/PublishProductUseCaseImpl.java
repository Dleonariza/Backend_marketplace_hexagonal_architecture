package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.PublishProductUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public class PublishProductUseCaseImpl extends ProductUseCaseSupport implements PublishProductUseCase {

    public PublishProductUseCaseImpl(ProductRepositoryPort productRepo) { super(productRepo); }

    @Override
    public Product publishProduct(ProductId id, SellerProfileId sellerId) {
        Product product = loadAndVerifyOwnership(id, sellerId);
        product.publish();
        return productRepo.save(product);
    }
}
