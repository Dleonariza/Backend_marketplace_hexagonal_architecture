package com.marketplace.hexagonal.Domain.Favorite.Exceptions;

import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public class FavoriteAlreadyExistsException extends RuntimeException {
    public FavoriteAlreadyExistsException(UserId userId, ProductId productId) {
        super("Product " + productId.getValue() + " is already in favorites for user " + userId.getValue());
    }
}
