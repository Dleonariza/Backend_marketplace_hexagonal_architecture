package com.marketplace.hexagonal.Application.Favorite;

import com.marketplace.hexagonal.Application.Favorite.UseCases.RemoveFavoriteUseCaseImpl;
import com.marketplace.hexagonal.Domain.Favorite.Exceptions.FavoriteNotFoundException;
import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.Favorite.Models.FavoriteId;
import com.marketplace.hexagonal.Domain.Favorite.Ports.Out.FavoriteRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveFavoriteUseCaseImplTest {

    @Mock FavoriteRepositoryPort favoriteRepo;
    RemoveFavoriteUseCaseImpl useCase;

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final ProductId PRODUCT_ID = ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");

    @BeforeEach
    void setUp() {
        useCase = new RemoveFavoriteUseCaseImpl(favoriteRepo);
    }

    @Test
    void removeFavorite_existingFavorite_callsDelete() {
        Favorite favorite = buildFavorite();
        when(favoriteRepo.findByUserIdAndProductId(USER_ID, PRODUCT_ID))
                .thenReturn(Optional.of(favorite));

        useCase.removeFavorite(USER_ID, PRODUCT_ID);

        verify(favoriteRepo).deleteById(favorite.getFavoriteId());
    }

    @Test
    void removeFavorite_notInFavorites_throwsFavoriteNotFoundException() {
        when(favoriteRepo.findByUserIdAndProductId(USER_ID, PRODUCT_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.removeFavorite(USER_ID, PRODUCT_ID))
                .isInstanceOf(FavoriteNotFoundException.class);
        verify(favoriteRepo, never()).deleteById(any());
    }

    private Favorite buildFavorite() {
        return Favorite.builder()
                .favoriteId(FavoriteId.generate())
                .userId(USER_ID)
                .productId(PRODUCT_ID)
                .createdAt(Instant.now())
                .build();
    }
}
