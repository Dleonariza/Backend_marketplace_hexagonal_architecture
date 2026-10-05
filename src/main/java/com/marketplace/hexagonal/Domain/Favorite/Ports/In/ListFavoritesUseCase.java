package com.marketplace.hexagonal.Domain.Favorite.Ports.In;

import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;

public interface ListFavoritesUseCase {
    List<Favorite> listFavorites(UserId userId);
}
