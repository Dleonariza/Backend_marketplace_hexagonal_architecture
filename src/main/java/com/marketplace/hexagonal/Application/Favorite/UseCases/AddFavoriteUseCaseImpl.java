package com.marketplace.hexagonal.Application.Favorite.UseCases;

import com.marketplace.hexagonal.Domain.Favorite.Exceptions.FavoriteAlreadyExistsException;
import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.Favorite.Ports.In.AddFavoriteUseCase;
import com.marketplace.hexagonal.Domain.Favorite.Ports.Out.FavoriteRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddFavoriteUseCaseImpl implements AddFavoriteUseCase {

    private final FavoriteRepositoryPort favoriteRepo;
    private final ProductRepositoryPort productRepo;

    @Override
    public Favorite addFavorite(UserId userId, ProductId productId) {
        if (favoriteRepo.existsByUserIdAndProductId(userId, productId))
            throw new FavoriteAlreadyExistsException(userId, productId);

        productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Favorite favorite = Favorite.create(userId, productId);
        return favoriteRepo.save(favorite);
    }
}
