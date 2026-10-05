package com.marketplace.hexagonal.Domain.Cart;

import com.marketplace.hexagonal.Domain.Cart.Exceptions.CartAlreadyCheckedOutException;
import com.marketplace.hexagonal.Domain.Cart.Exceptions.CartItemNotFoundException;
import com.marketplace.hexagonal.Domain.Cart.Models.*;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CartTest {

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final ProductId PRODUCT_A = ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final ProductId PRODUCT_B = ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304d");
    private static final SellerProfileId SELLER_ID = SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002");
    private static final Money PRICE = Money.of(new BigDecimal("50.00"), "USD");

    private Cart cart;

    @BeforeEach
    void setUp() { cart = Cart.create(USER_ID); }

    @Test
    void create_shouldInitializeWithActiveStatusAndEmptyItems() {
        assertThat(cart.getStatus()).isEqualTo(CartStatus.ACTIVE);
        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getId()).isNotNull();
        assertThat(cart.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    void addItem_newProduct_shouldAddToItemList() {
        cart.addItem(PRODUCT_A, null, null, null, SELLER_ID, 2, PRICE, "Product A", "product-a", null);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().getFirst().getProductId()).isEqualTo(PRODUCT_A);
        assertThat(cart.getItems().getFirst().getQuantity()).isEqualTo(2);
    }

    @Test
    void addItem_sameProductTwice_shouldIncreaseQuantity() {
        cart.addItem(PRODUCT_A, null, null, null, SELLER_ID, 2, PRICE, "Product A", "product-a", null);
        cart.addItem(PRODUCT_A, null, null, null, SELLER_ID, 3, PRICE, "Product A", "product-a", null);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().getFirst().getQuantity()).isEqualTo(5);
    }

    @Test
    void addItem_differentVariants_shouldAddAsSeparateItems() {
        ProductVariantId v1 = ProductVariantId.of("660e8400-e29b-41d4-a716-446655440001");
        ProductVariantId v2 = ProductVariantId.of("660e8400-e29b-41d4-a716-446655440002");

        cart.addItem(PRODUCT_A, v1, null, null, SELLER_ID, 1, PRICE, "Product A", "product-a", null);
        cart.addItem(PRODUCT_A, v2, null, null, SELLER_ID, 1, PRICE, "Product A", "product-a", null);

        assertThat(cart.getItems()).hasSize(2);
    }

    @Test
    void addItem_differentProducts_shouldAddBothItems() {
        cart.addItem(PRODUCT_A, null, null, null, SELLER_ID, 1, PRICE, "Product A", "product-a", null);
        cart.addItem(PRODUCT_B, null, null, null, SELLER_ID, 1, PRICE, "Product B", "product-b", null);

        assertThat(cart.getItems()).hasSize(2);
    }

    @Test
    void addItem_toCheckedOutCart_shouldThrow() {
        cart.checkout();
        assertThatThrownBy(() ->
                cart.addItem(PRODUCT_A, null, null, null, SELLER_ID, 1, PRICE, "Product A", "product-a", null))
                .isInstanceOf(CartAlreadyCheckedOutException.class);
    }

    @Test
    void removeItem_shouldRemoveFromList() {
        CartItem item = cart.addItem(PRODUCT_A, null, null, null, SELLER_ID, 1, PRICE, "Product A", "product-a", null);
        cart.removeItem(item.getId());

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void removeItem_nonExistentId_shouldThrow() {
        assertThatThrownBy(() -> cart.removeItem(CartItemId.generate()))
                .isInstanceOf(CartItemNotFoundException.class);
    }

    @Test
    void updateItemQuantity_shouldChangeQuantity() {
        CartItem item = cart.addItem(PRODUCT_A, null, null, null, SELLER_ID, 1, PRICE, "Product A", "product-a", null);
        cart.updateItemQuantity(item.getId(), 5);

        assertThat(cart.getItems().getFirst().getQuantity()).isEqualTo(5);
    }

    @Test
    void updateItemQuantity_toZero_shouldThrow() {
        CartItem item = cart.addItem(PRODUCT_A, null, null, null, SELLER_ID, 1, PRICE, "Product A", "product-a", null);
        assertThatThrownBy(() -> cart.updateItemQuantity(item.getId(), 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void clear_shouldRemoveAllItems() {
        cart.addItem(PRODUCT_A, null, null, null, SELLER_ID, 1, PRICE, "Product A", "product-a", null);
        cart.addItem(PRODUCT_B, null, null, null, SELLER_ID, 2, PRICE, "Product B", "product-b", null);
        cart.clear();

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void getTotal_shouldSumAllItemSubtotals() {
        cart.addItem(PRODUCT_A, null, null, null, SELLER_ID, 2, Money.of(new BigDecimal("10.00"), "USD"), "A", "a", null);
        cart.addItem(PRODUCT_B, null, null, null, SELLER_ID, 3, Money.of(new BigDecimal("20.00"), "USD"), "B", "b", null);

        assertThat(cart.getTotal().amount()).isEqualByComparingTo("80.00");
    }

    @Test
    void getTotal_emptyCart_shouldReturnZero() {
        assertThat(cart.getTotal().amount()).isEqualByComparingTo("0.00");
    }

    @Test
    void checkout_shouldTransitionToCheckedOut() {
        cart.checkout();
        assertThat(cart.getStatus()).isEqualTo(CartStatus.CHECKED_OUT);
    }

    @Test
    void abandon_shouldTransitionToAbandoned() {
        cart.abandon();
        assertThat(cart.getStatus()).isEqualTo(CartStatus.ABANDONED);
    }

    @Test
    void checkout_alreadyCheckedOut_shouldThrow() {
        cart.checkout();
        assertThatThrownBy(() -> cart.checkout())
                .isInstanceOf(CartAlreadyCheckedOutException.class);
    }

    @Test
    void create_shouldSetExpiresAtSevenDaysFromNow() {
        Instant expectedExpiry = Instant.now().plus(Cart.TTL_DAYS, ChronoUnit.DAYS);
        assertThat(cart.getExpiresAt()).isAfter(expectedExpiry.minusSeconds(5));
        assertThat(cart.getExpiresAt()).isBefore(expectedExpiry.plusSeconds(5));
    }

    @Test
    void create_shouldNotBeExpired() {
        assertThat(cart.isExpired()).isFalse();
    }

    @Test
    void addItem_shouldRefreshExpiry() throws InterruptedException {
        Instant before = cart.getExpiresAt();
        Thread.sleep(50);
        cart.addItem(PRODUCT_A, null, null, null, SELLER_ID, 1, PRICE, "Product A", "product-a", null);
        assertThat(cart.getExpiresAt()).isAfterOrEqualTo(before);
    }

    @Test
    void expiredCart_isExpired_shouldReturnTrue() {
        Cart expiredCart = Cart.reconstitute(
                CartId.generate(), USER_ID, null, List.of(),
                CartStatus.ACTIVE, null,
                Instant.now().minus(10, ChronoUnit.DAYS),
                Instant.now().minus(10, ChronoUnit.DAYS),
                Instant.now().minus(3, ChronoUnit.DAYS));

        assertThat(expiredCart.isExpired()).isTrue();
    }

    @Test
    void activeCartWithFutureTtl_isExpired_shouldReturnFalse() {
        Cart activeCart = Cart.reconstitute(
                CartId.generate(), USER_ID, null, List.of(),
                CartStatus.ACTIVE, null,
                Instant.now(), Instant.now(),
                Instant.now().plus(5, ChronoUnit.DAYS));

        assertThat(activeCart.isExpired()).isFalse();
    }
}
