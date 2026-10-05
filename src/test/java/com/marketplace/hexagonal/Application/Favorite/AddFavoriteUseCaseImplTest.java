package com.marketplace.hexagonal.Application.Favorite;

import com.marketplace.hexagonal.Application.Favorite.UseCases.AddFavoriteUseCaseImpl;
import com.marketplace.hexagonal.Domain.Favorite.Exceptions.FavoriteAlreadyExistsException;
import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.Favorite.Models.FavoriteId;
import com.marketplace.hexagonal.Domain.Favorite.Ports.Out.FavoriteRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddFavoriteUseCaseImplTest {

    @Mock FavoriteRepositoryPort favoriteRepo;
    @Mock ProductRepositoryPort productRepo;
    AddFavoriteUseCaseImpl useCase;

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final ProductId PRODUCT_ID = ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");

    @BeforeEach
    void setUp() {
        useCase = new AddFavoriteUseCaseImpl(favoriteRepo, productRepo);
    }

    @Test
    void addFavorite_newProduct_savesFavorite() {
        when(favoriteRepo.existsByUserIdAndProductId(USER_ID, PRODUCT_ID)).thenReturn(false);
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(buildProduct()));
        when(favoriteRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Favorite result = useCase.addFavorite(USER_ID, PRODUCT_ID);

        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(result.getCreatedAt()).isNotNull();
        verify(favoriteRepo).save(any());
    }

    @Test
    void addFavorite_savesOnlyFkReference_noSnapshot() {
        when(favoriteRepo.existsByUserIdAndProductId(USER_ID, PRODUCT_ID)).thenReturn(false);
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(buildProduct()));
        when(favoriteRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Favorite result = useCase.addFavorite(USER_ID, PRODUCT_ID);

        assertThat(result.getProduct()).isNull();
    }

    @Test
    void addFavorite_alreadyFavorited_throwsFavoriteAlreadyExistsException() {
        when(favoriteRepo.existsByUserIdAndProductId(USER_ID, PRODUCT_ID)).thenReturn(true);

        assertThatThrownBy(() -> useCase.addFavorite(USER_ID, PRODUCT_ID))
                .isInstanceOf(FavoriteAlreadyExistsException.class);
        verify(productRepo, never()).findById(any());
        verify(favoriteRepo, never()).save(any());
    }

    @Test
    void addFavorite_productNotFound_throwsProductNotFoundException() {
        when(favoriteRepo.existsByUserIdAndProductId(USER_ID, PRODUCT_ID)).thenReturn(false);
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.addFavorite(USER_ID, PRODUCT_ID))
                .isInstanceOf(ProductNotFoundException.class);
        verify(favoriteRepo, never()).save(any());
    }

    @Test
    void addFavorite_duplicateCheckRunsBeforeProductLookup() {
        when(favoriteRepo.existsByUserIdAndProductId(USER_ID, PRODUCT_ID)).thenReturn(true);

        assertThatThrownBy(() -> useCase.addFavorite(USER_ID, PRODUCT_ID))
                .isInstanceOf(FavoriteAlreadyExistsException.class);
        verifyNoInteractions(productRepo);
    }

    private Product buildProduct() {
        return Product.builder()
                .id(PRODUCT_ID)
                .name("Test Product")
                .slug("test-product")
                .basePrice(Money.of(new BigDecimal("99.99"), "USD"))
                .images(List.of())
                .build();
    }
}
