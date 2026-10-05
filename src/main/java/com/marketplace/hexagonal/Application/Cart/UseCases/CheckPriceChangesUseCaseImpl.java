package com.marketplace.hexagonal.Application.Cart.UseCases;

import com.marketplace.hexagonal.Domain.Cart.Models.*;
import com.marketplace.hexagonal.Domain.Cart.Ports.In.CheckPriceChangesUseCase;
import com.marketplace.hexagonal.Domain.Product.Models.*;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CheckPriceChangesUseCaseImpl implements CheckPriceChangesUseCase {

    private final ProductRepositoryPort productRepo;

    public CheckPriceChangesUseCaseImpl(ProductRepositoryPort productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public List<PriceChangeInfo> checkPriceChanges(Cart cart) {
        return cart.getItems().stream()
                .map(this::evaluate)
                .collect(Collectors.toList());
    }

    private PriceChangeInfo evaluate(CartItem item) {
        Optional<Product> productOpt = productRepo.findById(item.getProductId());

        if (productOpt.isEmpty()) {
            return PriceChangeInfo.productGone(item.getId(), item.getUnitPrice());
        }

        Product product = productOpt.get();
        Money currentPrice = resolveCurrentPrice(product, item.getVariantId());

        boolean changed = currentPrice.amount()
                .compareTo(item.getUnitPrice().amount()) != 0;

        return changed
                ? PriceChangeInfo.changed(item.getId(), item.getUnitPrice(), currentPrice)
                : PriceChangeInfo.unchanged(item.getId(), item.getUnitPrice());
    }

    private Money resolveCurrentPrice(Product product, ProductVariantId variantId) {
        if (variantId != null) {
            return product.getVariants().stream()
                    .filter(v -> v.getId().equals(variantId))
                    .findFirst()
                    .map(v -> v.getPrice() != null ? v.getPrice() : product.getBasePrice())
                    .orElse(product.getBasePrice());
        }
        return product.getBasePrice();
    }
}
