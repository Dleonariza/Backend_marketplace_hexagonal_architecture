package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.AddProductImageUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public class AddProductImageUseCaseImpl extends ProductUseCaseSupport implements AddProductImageUseCase {

    public AddProductImageUseCaseImpl(ProductRepositoryPort productRepo) { super(productRepo); }

    @Override
    public Product addImage(ProductId id, SellerProfileId sellerId, String url, String altText) {
        Product product = loadAndVerifyOwnership(id, sellerId);
        product.addImage(url, altText);
        return productRepo.save(product);
    }
}
