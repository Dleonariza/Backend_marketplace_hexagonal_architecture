package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.DeleteProductUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public class DeleteProductUseCaseImpl extends ProductUseCaseSupport implements DeleteProductUseCase {

    public DeleteProductUseCaseImpl(ProductRepositoryPort productRepo) { super(productRepo); }

    @Override
    public void deleteProduct(ProductId id, SellerProfileId sellerId) {
        Product product = loadAndVerifyOwnership(id, sellerId);
        product.delete();
        productRepo.save(product);
    }
}
