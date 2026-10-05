package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.RemoveProductVariantUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

public class RemoveProductVariantUseCaseImpl extends ProductUseCaseSupport implements RemoveProductVariantUseCase {

    public RemoveProductVariantUseCaseImpl(ProductRepositoryPort productRepo) { super(productRepo); }

    @Override
    public Product removeVariant(ProductId id, SellerProfileId sellerId, ProductVariantId variantId) {
        Product product = loadAndVerifyOwnership(id, sellerId);
        product.removeVariant(variantId);
        return productRepo.save(product);
    }
}
