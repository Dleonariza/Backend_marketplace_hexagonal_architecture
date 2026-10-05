package com.marketplace.hexagonal.Application.Favorite.Services;

import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.Favorite.Ports.In.AddFavoriteUseCase;
import com.marketplace.hexagonal.Domain.Favorite.Ports.In.ListFavoritesUseCase;
import com.marketplace.hexagonal.Domain.Favorite.Ports.In.RemoveFavoriteUseCase;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class FavoriteService {

    private final AddFavoriteUseCase addFavoriteUseCase;
    private final RemoveFavoriteUseCase removeFavoriteUseCase;
    private final ListFavoritesUseCase listFavoritesUseCase;

    public Favorite addFavorite(UserId userId, ProductId productId) {
        return addFavoriteUseCase.addFavorite(userId, productId);
    }

    public void removeFavorite(UserId userId, ProductId productId) {
        removeFavoriteUseCase.removeFavorite(userId, productId);
    }

    public List<Favorite> listFavorites(UserId userId) {
        return listFavoritesUseCase.listFavorites(userId);
    }
}
