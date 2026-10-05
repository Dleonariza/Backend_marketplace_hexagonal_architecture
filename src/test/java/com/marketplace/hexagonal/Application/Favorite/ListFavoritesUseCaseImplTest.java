package com.marketplace.hexagonal.Application.Favorite;

import com.marketplace.hexagonal.Application.Favorite.UseCases.ListFavoritesUseCaseImpl;
import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.Favorite.Models.FavoriteId;
import com.marketplace.hexagonal.Domain.Favorite.Ports.Out.FavoriteRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListFavoritesUseCaseImplTest {

    @Mock FavoriteRepositoryPort favoriteRepo;
    ListFavoritesUseCaseImpl useCase;

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final ProductId PRODUCT_ID = ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");

    @BeforeEach
    void setUp() {
        useCase = new ListFavoritesUseCaseImpl(favoriteRepo);
    }

    @Test
    void listFavorites_returnsAllUserFavoritesWithProductData() {
        List<Favorite> favorites = List.of(buildFavorite(), buildFavorite());
        when(favoriteRepo.findAllByUserId(USER_ID)).thenReturn(favorites);

        List<Favorite> result = useCase.listFavorites(USER_ID);

        assertThat(result).hasSize(2);

        assertThat(result.get(0).getProduct()).isNotNull();
        assertThat(result.get(0).getProduct().getName()).isEqualTo("Test Product");
    }

    @Test
    void listFavorites_noFavorites_returnsEmptyList() {
        when(favoriteRepo.findAllByUserId(USER_ID)).thenReturn(List.of());

        assertThat(useCase.listFavorites(USER_ID)).isEmpty();
    }

    private Favorite buildFavorite() {
        Product product = Product.builder()
                .id(PRODUCT_ID)
                .name("Test Product")
                .slug("test-product")
                .basePrice(Money.of(new BigDecimal("49.99"), "USD"))
                .images(List.of())
                .build();

        return Favorite.builder()
                .favoriteId(FavoriteId.generate())
                .userId(USER_ID)
                .productId(PRODUCT_ID)
                .product(product)
                .createdAt(Instant.now())
                .build();
    }
}
