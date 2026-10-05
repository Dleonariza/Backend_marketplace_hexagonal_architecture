package com.marketplace.hexagonal.Application.Cart;

import com.marketplace.hexagonal.Application.Cart.UseCases.*;
import com.marketplace.hexagonal.Domain.Cart.Exceptions.InsufficientStockException;
import com.marketplace.hexagonal.Domain.Cart.Models.*;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.StockReservationRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.*;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Application.Cart.Services.CartService;
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
class CartServiceTest {

    @Mock CartRepositoryPort cartRepo;
    @Mock StockReservationRepositoryPort stockRepo;
    @Mock ProductRepositoryPort productRepo;
    CartService cartService;

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final String PRODUCT_ID = "bc66d237-f54d-4bad-b521-0eba3b06304c";
    private static final SellerProfileId SELLER_ID = SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002");
    private static final String CATEGORY_ID= "660e8400-e29b-41d4-a716-446655440001";

    @BeforeEach
    void setUp() {
        cartService = new CartService(
                new GetCartUseCaseImpl(cartRepo),
                new AddToCartUseCaseImpl(cartRepo, stockRepo, productRepo),
                new RemoveFromCartUseCaseImpl(cartRepo, stockRepo),
                new UpdateCartItemQuantityUseCaseImpl(cartRepo, stockRepo, productRepo),
                new ClearCartUseCaseImpl(cartRepo, stockRepo),
                new GetOrCreateAnonymousCartUseCaseImpl(cartRepo),
                new MergeCartsUseCaseImpl(cartRepo, stockRepo),
                new CheckPriceChangesUseCaseImpl(productRepo));
    }

    @Test
    void getOrCreateCart_whenCartExists_shouldReturnExistingCart() {
        Cart existing = Cart.create(USER_ID);
        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(existing));

        Cart result = cartService.getOrCreateCart(USER_ID);

        assertThat(result.getId()).isEqualTo(existing.getId());
        verify(cartRepo, never()).save(any());
    }

    @Test
    void getOrCreateCart_whenNoCart_shouldCreateAndSave() {
        Cart newCart = Cart.create(USER_ID);
        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.empty());
        when(cartRepo.save(any())).thenReturn(newCart);

        Cart result = cartService.getOrCreateCart(USER_ID);

        assertThat(result).isNotNull();
        verify(cartRepo).save(any());
    }

    @Test
    void addToCart_withSufficientStock_shouldAddItemAndReserveStock() {
        Cart cart = Cart.create(USER_ID);
        Product product = buildProduct(10);

        when(productRepo.findById(ProductId.of(PRODUCT_ID))).thenReturn(Optional.of(product));
        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(stockRepo.sumActiveReservedQuantity(any(), any())).thenReturn(0);
        when(cartRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(stockRepo.findActiveByCartAndProduct(any(), any(), any())).thenReturn(Optional.empty());
        when(stockRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.addToCart(USER_ID, null, PRODUCT_ID, null, 3);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(3);
        verify(stockRepo).save(any());
    }

    @Test
    void addToCart_withInsufficientStock_shouldThrow() {
        Cart cart = Cart.create(USER_ID);
        Product product = buildProduct(2);

        when(productRepo.findById(ProductId.of(PRODUCT_ID))).thenReturn(Optional.of(product));
        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(stockRepo.sumActiveReservedQuantity(any(), any())).thenReturn(0);

        assertThatThrownBy(() -> cartService.addToCart(USER_ID, null, PRODUCT_ID, null, 5))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void addToCart_sameProductTwice_shouldAccumulateQuantity() {
        Cart cart = Cart.create(USER_ID);
        Product product = buildProduct(20);

        when(productRepo.findById(ProductId.of(PRODUCT_ID))).thenReturn(Optional.of(product));
        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(stockRepo.sumActiveReservedQuantity(any(), any())).thenReturn(0);
        when(cartRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(stockRepo.findActiveByCartAndProduct(any(), any(), any())).thenReturn(Optional.empty());
        when(stockRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        cartService.addToCart(USER_ID, null, PRODUCT_ID, null, 2);
        Cart result = cartService.addToCart(USER_ID, null, PRODUCT_ID, null, 3);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(5);
    }

    @Test
    void removeFromCart_existingItem_shouldRemoveAndReleaseReservation() {
        Cart cart = Cart.create(USER_ID);
        CartItem item = cart.addItem(
                ProductId.of(PRODUCT_ID), null, null, null, SELLER_ID, 1,
                Money.of(BigDecimal.TEN, "USD"), "Product", "product", null);

        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(stockRepo.findActiveByCartAndProduct(any(), any(), any()))
                .thenReturn(Optional.of(buildReservation(cart.getId(), 1)));
        when(stockRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(cartRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.removeFromCart(USER_ID, null, item.getId().getValue());

        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void clearCart_shouldRemoveAllItemsAndReleaseReservations() {
        Cart cart = Cart.create(USER_ID);
        cart.addItem(ProductId.of(PRODUCT_ID), null, null, null, SELLER_ID, 1,
                Money.of(BigDecimal.TEN, "USD"), "Product", "product", null);

        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(cart));
        doNothing().when(stockRepo).releaseAllByCartId(any());
        when(cartRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.clearCart(USER_ID, null);

        assertThat(result.getItems()).isEmpty();
        verify(stockRepo).releaseAllByCartId(cart.getId());
    }

    private Product buildProduct(int variantStock) {
        ProductVariant variant = ProductVariant.builder()
                .id(ProductVariantId.of("aa66d237-f54d-4bad-b521-0eba3b06304c"))
                .sku("SKU-001")
                .price(Money.of(new BigDecimal("50.00"), "USD"))
                .stock(variantStock)
                .active(true)
                .build();

        return Product.reconstitute(
                ProductId.of(PRODUCT_ID),
                SELLER_ID,
                CategoryId.of(CATEGORY_ID),
                "Test Product", "Description", "test-product",
                Money.of(new BigDecimal("50.00"), "USD"),
                ProductType.PHYSICAL,
                ProductStatus.ACTIVE,
                List.of(variant),
                List.of(),
                Instant.now(), Instant.now());
    }

    private StockReservation buildReservation(CartId cartId, int quantity) {
        return StockReservation.reserve(
                cartId, ProductId.of(PRODUCT_ID), null, quantity, 30);
    }
}
