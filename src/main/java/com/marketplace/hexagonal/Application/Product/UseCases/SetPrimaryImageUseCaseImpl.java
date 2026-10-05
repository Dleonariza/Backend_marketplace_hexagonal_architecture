package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductImageId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.SetPrimaryImageUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public class SetPrimaryImageUseCaseImpl extends ProductUseCaseSupport implements SetPrimaryImageUseCase {

    public SetPrimaryImageUseCaseImpl(ProductRepositoryPort productRepo) { super(productRepo); }

    @Override
    public Product setPrimaryImage(ProductId id, SellerProfileId sellerId, ProductImageId imageId) {
        Product product = loadAndVerifyOwnership(id, sellerId);
        product.setPrimaryImage(imageId);
        return productRepo.save(product);
    }
}
