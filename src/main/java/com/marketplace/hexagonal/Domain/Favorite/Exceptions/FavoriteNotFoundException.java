package com.marketplace.hexagonal.Domain.Favorite.Exceptions;

import com.marketplace.hexagonal.Domain.Product.Models.ProductId;

public class FavoriteNotFoundException extends RuntimeException {
    public FavoriteNotFoundException(ProductId productId) {
        super("Product " + productId.getValue() + " is not in favorites");
    }
}
