package com.marketplace.hexagonal.Domain.Favorite.Ports.In;

import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface AddFavoriteUseCase {
    Favorite addFavorite(UserId userId, ProductId productId);
}
