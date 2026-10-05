package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.Favorite.Models.FavoriteId;
import com.marketplace.hexagonal.Domain.Favorite.Ports.Out.FavoriteRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.*;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Entities.Favorite.FavoriteEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.ProductEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.ProductImageEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaFavoriteRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaProductRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class JpaFavoriteRepositoryAdapter implements FavoriteRepositoryPort {

    private final JpaFavoriteRepository jpaFavoriteRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaProductRepository jpaProductRepository;

    @Override
    @Transactional
    public Favorite save(Favorite favorite) {
        UserEntity user = jpaUserRepository
                .findById(UUID.fromString(favorite.getUserId().getValue()))
                .orElseThrow(() -> new IllegalStateException(
                        "User not found: " + favorite.getUserId().getValue()));

        ProductEntity product = jpaProductRepository
                .findById(UUID.fromString(favorite.getProductId().getValue()))
                .orElseThrow(() -> new IllegalStateException(
                        "Product not found: " + favorite.getProductId().getValue()));

        FavoriteEntity entity = FavoriteEntity.builder()
                .user(user)
                .product(product)
                .createdAt(favorite.getCreatedAt())
                .build();

        return toDomain(jpaFavoriteRepository.save(entity));
    }

    @Override
    public List<Favorite> findAllByUserId(UserId userId) {
        return jpaFavoriteRepository
                .findAllByUser_IdOrderByCreatedAtDesc(UUID.fromString(userId.getValue()))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Favorite> findByUserIdAndProductId(UserId userId, ProductId productId) {
        return jpaFavoriteRepository
                .findByUser_IdAndProduct_Id(
                        UUID.fromString(userId.getValue()),
                        UUID.fromString(productId.getValue()))
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(FavoriteId id) {
        jpaFavoriteRepository.deleteFavoriteById(UUID.fromString(id.getValue()));
    }

    @Override
    public boolean existsByUserIdAndProductId(UserId userId, ProductId productId) {
        return jpaFavoriteRepository.existsByUser_IdAndProduct_Id(
                UUID.fromString(userId.getValue()),
                UUID.fromString(productId.getValue()));
    }

    private Favorite toDomain(FavoriteEntity entity) {
        ProductEntity pe = entity.getProduct();

        return Favorite.builder()
                .favoriteId(FavoriteId.of(entity.getId().toString()))
                .userId(UserId.of(entity.getUser().getId().toString()))
                .productId(ProductId.of(pe.getId().toString()))
                .product(toProductDomain(pe))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private Product toProductDomain(ProductEntity pe) {
        List<ProductImage> images = pe.getImages().stream()
                .map(img -> ProductImage.builder()
                        .id(ProductImageId.of(img.getId().toString()))
                        .url(img.getUrl())
                        .altText(img.getAltText())
                        .displayOrder(img.getDisplayOrder())
                        .primary(img.isPrimary())
                        .build())
                .toList();

        return Product.builder()
                .id(ProductId.of(pe.getId().toString()))
                .name(pe.getName())
                .slug(pe.getSlug())
                .basePrice(Money.of(pe.getBasePriceAmount(), pe.getBasePriceCurrency()))
                .images(images)
                .status(pe.getStatus())
                .build();
    }
}
