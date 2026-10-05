package com.marketplace.hexagonal.Infrastructura.Jobs;

import com.marketplace.hexagonal.Application.Cart.UseCases.ExpireCartsUseCaseImpl;
import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Models.CartId;
import com.marketplace.hexagonal.Domain.Cart.Models.CartStatus;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.StockReservationRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartExpirationJobTest {

    @Mock CartRepositoryPort cartRepo;
    @Mock StockReservationRepositoryPort stockRepo;
    CartExpirationJob job;

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");

    @BeforeEach
    void setUp() {
        ExpireCartsUseCaseImpl useCase = new ExpireCartsUseCaseImpl(cartRepo, stockRepo);
        job = new CartExpirationJob(useCase);
    }

    @Test
    void run_noExpiredCarts_doesNothing() {
        when(cartRepo.findExpiredActive()).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> job.run());

        verify(stockRepo, never()).releaseAllByCartId(any());
        verify(cartRepo, never()).save(any());
    }

    @Test
    void run_oneExpiredCart_releasesStockAndAbandons() {
        Cart cart = Cart.create(USER_ID);
        when(cartRepo.findExpiredActive()).thenReturn(List.of(cart));
        when(cartRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        job.run();

        verify(stockRepo).releaseAllByCartId(cart.getId());
        verify(cartRepo).save(cart);
        assertThat(cart.getStatus()).isEqualTo(CartStatus.ABANDONED);
    }

    @Test
    void run_multipleExpiredCarts_processesAll() {
        Cart cart1 = Cart.create(USER_ID);
        Cart cart2 = Cart.create(UserId.of("660e8400-e29b-41d4-a716-446655440001"));
        when(cartRepo.findExpiredActive()).thenReturn(List.of(cart1, cart2));
        when(cartRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        job.run();

        verify(stockRepo).releaseAllByCartId(cart1.getId());
        verify(stockRepo).releaseAllByCartId(cart2.getId());
        verify(cartRepo, times(2)).save(any());
        assertThat(cart1.getStatus()).isEqualTo(CartStatus.ABANDONED);
        assertThat(cart2.getStatus()).isEqualTo(CartStatus.ABANDONED);
    }

    @Test
    void run_useCaseThrows_doesNotPropagateException() {
        when(cartRepo.findExpiredActive()).thenThrow(new RuntimeException("DB down"));

        assertThatNoException().isThrownBy(() -> job.run());
    }
}
