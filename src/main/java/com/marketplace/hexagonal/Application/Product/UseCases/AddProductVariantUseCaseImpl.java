package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.AddProductVariantUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

import java.util.Map;

public class AddProductVariantUseCaseImpl extends ProductUseCaseSupport implements AddProductVariantUseCase {

    public AddProductVariantUseCaseImpl(ProductRepositoryPort productRepo) { super(productRepo); }

    @Override
    public Product addVariant(ProductId id, SellerProfileId sellerId,
                              String sku, Map<String, String> attributes, Money price, int stock) {
        Product product = loadAndVerifyOwnership(id, sellerId);
        product.addVariant(sku, attributes, price, stock);
        return productRepo.save(product);
    }
}
