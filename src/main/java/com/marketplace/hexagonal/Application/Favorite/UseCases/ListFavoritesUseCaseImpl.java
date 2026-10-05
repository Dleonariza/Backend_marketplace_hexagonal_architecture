package com.marketplace.hexagonal.Application.Favorite.UseCases;

import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.Favorite.Ports.In.ListFavoritesUseCase;
import com.marketplace.hexagonal.Domain.Favorite.Ports.Out.FavoriteRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ListFavoritesUseCaseImpl implements ListFavoritesUseCase {

    private final FavoriteRepositoryPort favoriteRepo;

    @Override
    public List<Favorite> listFavorites(UserId userId) {
        return favoriteRepo.findAllByUserId(userId);
    }
}
