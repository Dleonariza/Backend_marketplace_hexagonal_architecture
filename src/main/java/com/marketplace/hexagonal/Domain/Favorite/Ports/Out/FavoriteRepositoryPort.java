package com.marketplace.hexagonal.Domain.Favorite.Ports.Out;

import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.Favorite.Models.FavoriteId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepositoryPort {
    Favorite save(Favorite favorite);
    List<Favorite> findAllByUserId(UserId userId);
    Optional<Favorite> findByUserIdAndProductId(UserId userId, ProductId productId);
    void deleteById(FavoriteId id);
    boolean existsByUserIdAndProductId(UserId userId, ProductId productId);
}
