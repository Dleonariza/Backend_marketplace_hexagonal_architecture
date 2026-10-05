package com.marketplace.hexagonal.Application.Favorite.UseCases;

import com.marketplace.hexagonal.Domain.Favorite.Exceptions.FavoriteNotFoundException;
import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.Favorite.Ports.In.RemoveFavoriteUseCase;
import com.marketplace.hexagonal.Domain.Favorite.Ports.Out.FavoriteRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RemoveFavoriteUseCaseImpl implements RemoveFavoriteUseCase {

    private final FavoriteRepositoryPort favoriteRepo;

    @Override
    public void removeFavorite(UserId userId, ProductId productId) {
        Favorite favorite = favoriteRepo.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new FavoriteNotFoundException(productId));

        favoriteRepo.deleteById(favorite.getFavoriteId());
    }
}
