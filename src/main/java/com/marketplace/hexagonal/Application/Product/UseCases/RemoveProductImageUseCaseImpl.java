package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductImageId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.RemoveProductImageUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public class RemoveProductImageUseCaseImpl extends ProductUseCaseSupport implements RemoveProductImageUseCase {

    public RemoveProductImageUseCaseImpl(ProductRepositoryPort productRepo) { super(productRepo); }

    @Override
    public Product removeImage(ProductId id, SellerProfileId sellerId, ProductImageId imageId) {
        Product product = loadAndVerifyOwnership(id, sellerId);
        product.removeImage(imageId);
        return productRepo.save(product);
    }
}
