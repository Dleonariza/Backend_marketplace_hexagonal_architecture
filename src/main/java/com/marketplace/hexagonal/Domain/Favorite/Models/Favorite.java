package com.marketplace.hexagonal.Domain.Favorite.Models;

import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Favorite {

    private FavoriteId favoriteId;
    private UserId userId;
    private ProductId productId;

    private Product product;

    private Instant createdAt;

    public static Favorite create(UserId userId, ProductId productId) {
        Favorite f = new Favorite();
        f.favoriteId = FavoriteId.generate();
        f.userId = userId;
        f.productId = productId;
        f.createdAt = Instant.now();
        return f;
    }
}
