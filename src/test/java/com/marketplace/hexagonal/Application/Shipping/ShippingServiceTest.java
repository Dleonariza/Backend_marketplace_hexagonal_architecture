package com.marketplace.hexagonal.Application.Shipping;

import com.marketplace.hexagonal.Application.Shipping.Services.ShippingService;
import com.marketplace.hexagonal.Application.Shipping.UseCases.*;
import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.Shipping.Exceptions.ShippingOptionNotFoundException;
import com.marketplace.hexagonal.Domain.Shipping.Exceptions.ShippingOptionOwnershipException;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingQuote;
import com.marketplace.hexagonal.Domain.Shipping.Ports.Out.SellerShippingOptionRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock SellerShippingOptionRepositoryPort shippingRepo;
    @Mock CartRepositoryPort cartRepo;
    @Mock SellerProfileRepositoryPort sellerRepo;
    ShippingService shippingService;

    private static final SellerProfileId SELLER_ID = SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002");
    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final ProductId PRODUCT = ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final Money PRICE_5 = Money.of(new BigDecimal("5.00"), "USD");

    @BeforeEach
    void setUp() {
        shippingService = new ShippingService(
                new AddShippingOptionUseCaseImpl(shippingRepo),
                new UpdateShippingOptionUseCaseImpl(shippingRepo),
                new RemoveShippingOptionUseCaseImpl(shippingRepo),
                new ToggleShippingOptionUseCaseImpl(shippingRepo),
                new ListSellerShippingOptionsUseCaseImpl(shippingRepo),
                new CalculateShippingUseCaseImpl(cartRepo, shippingRepo, sellerRepo)
        );
    }

    @Test
    void addShippingOption_shouldSaveAndReturn() {
        SellerShippingOption saved = buildOption("Standard", 3, 5);
        when(shippingRepo.save(any())).thenReturn(saved);

        SellerShippingOption result = shippingService.addShippingOption(
                SELLER_ID, "Standard", "desc", PRICE_5, 3, 5);

        assertThat(result.getName()).isEqualTo("Standard");
        verify(shippingRepo).save(any());
    }

    @Test
    void addShippingOption_invalidName_shouldThrow() {
        assertThatThrownBy(() ->
                shippingService.addShippingOption(SELLER_ID, "", null, PRICE_5, 1, 3))
                .isInstanceOf(IllegalArgumentException.class);
        verify(shippingRepo, never()).save(any());
    }

    @Test
    void updateShippingOption_ownerSeller_shouldUpdate() {
        SellerShippingOption existing = buildOption("Old Name", 3, 5);
        when(shippingRepo.findById(any())).thenReturn(Optional.of(existing));
        when(shippingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SellerShippingOption result = shippingService.updateShippingOption(
                SELLER_ID, existing.getId().getValue(),
                "New Name", "desc", PRICE_5, 1, 2);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEstimatedDaysMax()).isEqualTo(2);
    }

    @Test
    void updateShippingOption_differentSeller_shouldThrow() {
        SellerShippingOption existing = buildOption("Standard", 3, 5);
        when(shippingRepo.findById(any())).thenReturn(Optional.of(existing));
        SellerProfileId otherSeller = SellerProfileId.of("999e8400-e29b-41d4-a716-446655440099");

        assertThatThrownBy(() ->
                shippingService.updateShippingOption(
                        otherSeller, existing.getId().getValue(),
                        "Hack", null, PRICE_5, 1, 2))
                .isInstanceOf(ShippingOptionOwnershipException.class);
    }

    @Test
    void updateShippingOption_notFound_shouldThrow() {
        when(shippingRepo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                shippingService.updateShippingOption(
                        SELLER_ID, "non-existent-id", "Name", null, PRICE_5, 1, 2))
                .isInstanceOf(ShippingOptionNotFoundException.class);
    }

    @Test
    void removeShippingOption_ownerSeller_shouldDelete() {
        SellerShippingOption existing = buildOption("Standard", 3, 5);
        when(shippingRepo.findById(any())).thenReturn(Optional.of(existing));

        shippingService.removeShippingOption(SELLER_ID, existing.getId().getValue());

        verify(shippingRepo).deleteById(any());
    }

    @Test
    void removeShippingOption_differentSeller_shouldThrow() {
        SellerShippingOption existing = buildOption("Standard", 3, 5);
        when(shippingRepo.findById(any())).thenReturn(Optional.of(existing));
        SellerProfileId other = SellerProfileId.of("999e8400-e29b-41d4-a716-446655440099");

        assertThatThrownBy(() ->
                shippingService.removeShippingOption(other, existing.getId().getValue()))
                .isInstanceOf(ShippingOptionOwnershipException.class);
        verify(shippingRepo, never()).deleteById(any());
    }

    @Test
    void deactivateShippingOption_shouldSetInactive() {
        SellerShippingOption option = buildOption("Standard", 3, 5);
        when(shippingRepo.findById(any())).thenReturn(Optional.of(option));
        when(shippingRepo.save(any())).thenReturn(option);

        shippingService.deactivateShippingOption(SELLER_ID, option.getId().getValue());

        assertThat(option.isActive()).isFalse();
        verify(shippingRepo).save(option);
    }

    @Test
    void activateShippingOption_shouldSetActive() {
        SellerShippingOption option = buildOption("Standard", 3, 5);
        option.deactivate();
        when(shippingRepo.findById(any())).thenReturn(Optional.of(option));
        when(shippingRepo.save(any())).thenReturn(option);

        shippingService.activateShippingOption(SELLER_ID, option.getId().getValue());

        assertThat(option.isActive()).isTrue();
    }

    @Test
    void listShippingOptions_shouldReturnAllOptionsForSeller() {
        when(shippingRepo.findBySellerId(SELLER_ID)).thenReturn(List.of(
                buildOption("Standard", 3, 5),
                buildOption("Express", 1, 2)));

        List<SellerShippingOption> result = shippingService.listShippingOptions(SELLER_ID);

        assertThat(result).hasSize(2);
    }

    @Test
    void calculateShipping_cartWithItems_shouldReturnQuotesPerSeller() {
        Cart cart = cartWithItem();
        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(shippingRepo.findActivesBySellerId(SELLER_ID))
                .thenReturn(List.of(buildOption("Standard", 3, 5)));
        when(sellerRepo.findById(SELLER_ID))
                .thenReturn(Optional.of(mockSellerProfile("My Store")));

        List<ShippingQuote> quotes = shippingService.calculateShipping(USER_ID, null);

        assertThat(quotes).hasSize(1);
        assertThat(quotes.getFirst().storeName()).isEqualTo("My Store");
        assertThat(quotes.getFirst().options()).hasSize(1);
        assertThat(quotes.getFirst().itemsSubtotal().amount()).isEqualByComparingTo("100.00");
    }

    @Test
    void calculateShipping_emptyCart_shouldReturnEmptyList() {
        Cart emptyCart = Cart.create(USER_ID);
        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(emptyCart));

        List<ShippingQuote> quotes = shippingService.calculateShipping(USER_ID, null);

        assertThat(quotes).isEmpty();
    }

    @Test
    void calculateShipping_anonymousSession_shouldFindCartBySessionId() {
        String sessionId = "test-session";
        Cart cart = cartWithItemAnonymous(sessionId);
        when(cartRepo.findActiveBySessionId(sessionId)).thenReturn(Optional.of(cart));
        when(shippingRepo.findActivesBySellerId(SELLER_ID)).thenReturn(List.of());
        when(sellerRepo.findById(SELLER_ID)).thenReturn(Optional.of(mockSellerProfile("Store")));

        List<ShippingQuote> quotes = shippingService.calculateShipping(null, sessionId);

        assertThat(quotes).hasSize(1);
    }

    @Test
    void calculateShipping_sellerHasNoActiveOptions_returnsQuoteWithEmptyOptions() {
        Cart cart = cartWithItem();
        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(shippingRepo.findActivesBySellerId(SELLER_ID)).thenReturn(List.of());
        when(sellerRepo.findById(SELLER_ID)).thenReturn(Optional.of(mockSellerProfile("Store")));

        List<ShippingQuote> quotes = shippingService.calculateShipping(USER_ID, null);

        assertThat(quotes).hasSize(1);
        assertThat(quotes.getFirst().options()).isEmpty();
    }

    private SellerShippingOption buildOption(String name, int min, int max) {
        return SellerShippingOption.create(SELLER_ID, name, null, PRICE_5, min, max);
    }

    private Cart cartWithItem() {
        Cart cart = Cart.create(USER_ID);
        cart.addItem(PRODUCT, null, null, null, SELLER_ID, 2,
                Money.of(new BigDecimal("50.00"), "USD"), "Product", "product", null);
        return cart;
    }

    private Cart cartWithItemAnonymous(String sessionId) {
        Cart cart = Cart.createAnonymous(sessionId);
        cart.addItem(PRODUCT, null, null, null, SELLER_ID, 1,
                Money.of(new BigDecimal("30.00"), "USD"), "Product", "product", null);
        return cart;
    }

    private SellerProfile mockSellerProfile(String storeName) {
        return SellerProfile.builder()
                .id(SELLER_ID)
                .storeName(storeName)
                .build();
    }
}
