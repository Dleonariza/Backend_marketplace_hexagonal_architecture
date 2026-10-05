package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Cart.Models.*;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Entities.Cart.CartEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Cart.CartItemEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.ProductEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.ProductVariantEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Seller.SellerProfileEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import com.marketplace.hexagonal.Infrastructura.Mappers.CartMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JpaCartRepositoryAdapter implements CartRepositoryPort {

    private final JpaCartRepository jpaCartRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaProductRepository jpaProductRepository;
    private final JpaSellerProfileRepository jpaSellerProfileRepository;
    private final CartMapper cartMapper;

    @Override
    public Cart save(Cart cart) {
        UserEntity user = null;
        if (cart.getUserId() != null) {
            user = jpaUserRepository.findById(UUID.fromString(cart.getUserId().getValue()))
                    .orElseThrow(() -> new IllegalStateException(
                            "User not found: " + cart.getUserId().getValue()));
        }

        com.marketplace.hexagonal.Domain.Coupon.Models.AppliedCoupon ac = cart.getAppliedCoupon();

        CartEntity entity = CartEntity.builder()
                .id(UUID.fromString(cart.getId().getValue()))
                .user(user)
                .sessionId(cart.getSessionId())
                .status(cart.getStatus())
                .items(new ArrayList<>())
                .couponCode(ac != null ? ac.couponCode() : null)
                .couponDiscountType(ac != null ? ac.discountType() : null)
                .couponDiscountValue(ac != null ? ac.discountValue() : null)
                .couponDiscountAmount(ac != null ? ac.discountAmount().amount() : null)
                .couponDiscountCurrency(ac != null ? ac.discountAmount().currency() : null)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .expiresAt(cart.getExpiresAt())
                .build();

        if (cart.getItems() != null) {
            List<CartItemEntity> itemEntities = cart.getItems().stream()
                    .map(item -> {
                        ProductEntity product = jpaProductRepository
                                .findById(UUID.fromString(item.getProductId().getValue()))
                                .orElseThrow(() -> new IllegalStateException(
                                        "Product not found: " + item.getProductId().getValue()));

                        ProductVariantEntity variant = item.getVariantId() != null
                                ? product.getVariants().stream()
                                    .filter(v -> v.getId().equals(
                                            UUID.fromString(item.getVariantId().getValue())))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalStateException(
                                            "Variant not found: " + item.getVariantId().getValue()))
                                : null;

                        SellerProfileEntity seller = jpaSellerProfileRepository
                                .findById(UUID.fromString(item.getSellerId().getValue()))
                                .orElseThrow(() -> new IllegalStateException(
                                        "Seller not found: " + item.getSellerId().getValue()));

                        return cartMapper.toItemEntity(item, entity, product, variant, seller);
                    })
                    .collect(Collectors.toList());

            entity.getItems().addAll(itemEntities);
        }

        CartEntity saved = jpaCartRepository.save(entity);
        return cartMapper.toDomain(saved);
    }

    @Override
    public Optional<Cart> findById(CartId id) {
        return jpaCartRepository.findById(UUID.fromString(id.getValue()))
                .map(cartMapper::toDomain);
    }

    @Override
    public Optional<Cart> findActiveByUserId(UserId userId) {
        return jpaCartRepository
                .findByUser_IdAndStatusAndExpiresAtAfter(
                        UUID.fromString(userId.getValue()), CartStatus.ACTIVE, Instant.now())
                .map(cartMapper::toDomain);
    }

    @Override
    public Optional<Cart> findActiveBySessionId(String sessionId) {
        return jpaCartRepository
                .findBySessionIdAndStatusAndExpiresAtAfter(
                        sessionId, CartStatus.ACTIVE, Instant.now())
                .map(cartMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cart> findExpiredActive() {
        return jpaCartRepository.findExpiredActiveCarts(Instant.now())
                .stream()
                .map(cartMapper::toDomain)
                .collect(Collectors.toList());
    }
}
