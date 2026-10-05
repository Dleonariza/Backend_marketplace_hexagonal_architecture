package com.marketplace.hexagonal.Application.Cart.UseCases;

import com.marketplace.hexagonal.Domain.Cart.Exceptions.InsufficientStockException;
import com.marketplace.hexagonal.Domain.Cart.Exceptions.VariantRequiredException;
import com.marketplace.hexagonal.Domain.Cart.Models.*;
import com.marketplace.hexagonal.Domain.Cart.Ports.In.AddToCartUseCase;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.StockReservationRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Models.*;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.Map;
import java.util.Optional;

public class AddToCartUseCaseImpl implements AddToCartUseCase {

    private static final int RESERVATION_TTL_MINUTES = 30;

    private final CartRepositoryPort cartRepo;
    private final StockReservationRepositoryPort stockRepo;
    private final ProductRepositoryPort productRepo;

    public AddToCartUseCaseImpl(CartRepositoryPort cartRepo,
                                StockReservationRepositoryPort stockRepo,
                                ProductRepositoryPort productRepo) {
        this.cartRepo = cartRepo;
        this.stockRepo = stockRepo;
        this.productRepo = productRepo;
    }

    @Override
    public Cart addToCart(UserId userId, String sessionId, String productIdStr, String variantIdStr, int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");

        ProductId productId = ProductId.of(productIdStr);
        ProductVariantId variantId = variantIdStr != null ? ProductVariantId.of(variantIdStr) : null;

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (product.getStatus() != ProductStatus.ACTIVE)
            throw new IllegalArgumentException("Product is not available for purchase");

        if (variantId == null && product.getVariants().size() > 1)
            throw new VariantRequiredException(product.getName());

        Money unitPrice = resolvePrice(product, variantId);
        int stock = resolveStock(product, variantId);

        Cart cart = findActiveCart(userId, sessionId);

        int alreadyReservedInCart = cart.findItemByProduct(productId, variantId)
                .map(CartItem::getQuantity)
                .orElse(0);

        int globalReserved = stockRepo.sumActiveReservedQuantity(productId, variantId);
        int available = stock - (globalReserved - alreadyReservedInCart);

        if (available < quantity)
            throw new InsufficientStockException(
                    "Only " + available + " unit(s) available for this product");

        String primaryImageUrl = product.getImages().stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .map(ProductImage::getUrl)
                .orElse(null);

        String variantSku = null;
        Map<String, String> variantAttributes = null;

        if (variantId != null) {
            Optional<ProductVariant> variant = product.getVariants().stream()
                    .filter(v -> v.getId().equals(variantId))
                    .findFirst();
            variantSku = variant.map(ProductVariant::getSku).orElse(null);
            variantAttributes = variant.map(ProductVariant::getAttributes).orElse(null);
        }

        cart.addItem(
                productId, variantId, variantSku, variantAttributes,
                SellerProfileId.of(product.getSellerId().getValue()),
                quantity, unitPrice,
                product.getName(), product.getSlug(), primaryImageUrl);

        cart = cartRepo.save(cart);

        upsertReservation(cart, productId, variantId,
                alreadyReservedInCart + quantity);

        return cart;
    }

    private Cart findActiveCart(UserId userId, String sessionId) {
        if (userId != null) {
            return cartRepo.findActiveByUserId(userId)
                    .orElseGet(() -> cartRepo.save(Cart.create(userId)));
        }
        return cartRepo.findActiveBySessionId(sessionId)
                .orElseGet(() -> cartRepo.save(Cart.createAnonymous(sessionId)));
    }

    private Money resolvePrice(Product product, ProductVariantId variantId) {
        if (variantId != null) {
            return product.getVariants().stream()
                    .filter(v -> v.getId().equals(variantId))
                    .findFirst()
                    .map(v -> v.getPrice() != null ? v.getPrice() : product.getBasePrice())
                    .orElse(product.getBasePrice());
        }
        return product.getBasePrice();
    }

    private int resolveStock(Product product, ProductVariantId variantId) {
        if (variantId != null) {
            return product.getVariants().stream()
                    .filter(v -> v.getId().equals(variantId))
                    .findFirst()
                    .map(ProductVariant::getStock)
                    .orElse(0);
        }

        return product.getVariants().stream()
                .mapToInt(ProductVariant::getStock)
                .sum();
    }

    private void upsertReservation(Cart cart, ProductId productId,
                                   ProductVariantId variantId, int totalQuantity) {
        Optional<StockReservation> existing =
                stockRepo.findActiveByCartAndProduct(cart.getId(), productId, variantId);

        if (existing.isPresent()) {
            StockReservation r = existing.get();
            r.updateQuantity(totalQuantity);
            stockRepo.save(r);
        } else {
            stockRepo.save(StockReservation.reserve(
                    cart.getId(), productId, variantId,
                    totalQuantity, RESERVATION_TTL_MINUTES));
        }
    }
}
