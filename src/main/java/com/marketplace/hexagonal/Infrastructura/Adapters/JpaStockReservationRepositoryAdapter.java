package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Cart.Models.*;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.StockReservationRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import com.marketplace.hexagonal.Infrastructura.Entities.Cart.StockReservationEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.ProductEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.ProductVariantEntity;
import com.marketplace.hexagonal.Infrastructura.Mappers.CartMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaProductRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaStockReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class JpaStockReservationRepositoryAdapter implements StockReservationRepositoryPort {

    private final JpaStockReservationRepository jpaStockReservationRepository;
    private final JpaProductRepository jpaProductRepository;
    private final CartMapper cartMapper;

    @Override
    public StockReservation save(StockReservation reservation) {
        ProductEntity product = jpaProductRepository
                .findById(UUID.fromString(reservation.getProductId().getValue()))
                .orElseThrow(() -> new IllegalStateException(
                        "Product not found: " + reservation.getProductId().getValue()));

        ProductVariantEntity variant = reservation.getVariantId() != null
                ? product.getVariants().stream()
                    .filter(v -> v.getId().equals(
                            UUID.fromString(reservation.getVariantId().getValue())))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Variant not found: " + reservation.getVariantId().getValue()))
                : null;

        StockReservationEntity entity = StockReservationEntity.builder()
                .id(UUID.fromString(reservation.getId().getValue()))
                .cartId(UUID.fromString(reservation.getCartId().getValue()))
                .product(product)
                .variant(variant)
                .quantity(reservation.getQuantity())
                .status(reservation.getStatus())
                .expiresAt(reservation.getExpiresAt())
                .createdAt(reservation.getCreatedAt())
                .build();

        StockReservationEntity saved = jpaStockReservationRepository.save(entity);
        return cartMapper.toReservationDomain(saved);
    }

    @Override
    public Optional<StockReservation> findActiveByCartAndProduct(CartId cartId,
                                                                  ProductId productId,
                                                                  ProductVariantId variantId) {
        UUID variantUuid = variantId != null ? UUID.fromString(variantId.getValue()) : null;
        return jpaStockReservationRepository.findActiveByCartAndProduct(
                UUID.fromString(cartId.getValue()),
                UUID.fromString(productId.getValue()),
                variantUuid,
                Instant.now())
                .map(cartMapper::toReservationDomain);
    }

    @Override
    public int sumActiveReservedQuantity(ProductId productId, ProductVariantId variantId) {
        UUID variantUuid = variantId != null ? UUID.fromString(variantId.getValue()) : null;
        return jpaStockReservationRepository.sumActiveReservedQuantity(
                UUID.fromString(productId.getValue()),
                variantUuid,
                Instant.now());
    }

    @Override
    public void releaseAllByCartId(CartId cartId) {
        UUID cartUuid = UUID.fromString(cartId.getValue());
        jpaStockReservationRepository
                .findByCartIdAndStatus(cartUuid, StockReservationStatus.RESERVED)
                .forEach(r -> {
                    r.setStatus(StockReservationStatus.RELEASED);
                    jpaStockReservationRepository.save(r);
                });
    }
}
