package com.marketplace.hexagonal.Application.Product.UseCases;

import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import com.marketplace.hexagonal.Domain.Product.Ports.In.UpdateProductVariantUseCase;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;

import java.util.Map;

public class UpdateProductVariantUseCaseImpl extends ProductUseCaseSupport implements UpdateProductVariantUseCase {

    public UpdateProductVariantUseCaseImpl(ProductRepositoryPort productRepo) { super(productRepo); }

    @Override
    public Product updateVariant(ProductId id, SellerProfileId sellerId, ProductVariantId variantId,
                                 String sku, Map<String, String> attributes, Money price, int stock) {
        Product product = loadAndVerifyOwnership(id, sellerId);
        product.updateVariant(variantId, sku, attributes, price, stock);
        return productRepo.save(product);
    }
}
