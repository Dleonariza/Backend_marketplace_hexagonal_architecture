package com.marketplace.hexagonal.Domain.Order;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Order.Models.*;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class SubOrderTest {

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final SellerProfileId SELLER_ID = SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002");
    private static final OrderId ORDER_ID = OrderId.generate();
    private static final Money ZERO = Money.of(BigDecimal.ZERO, "USD");

    @Test
    void create_shouldSnapshotItemsAndComputeTotals() {
        SubOrder sub = buildSubOrder(2, new BigDecimal("30.00"), new BigDecimal("5.00"));

        assertThat(sub.getId()).isNotNull();
        assertThat(sub.getOrderId()).isEqualTo(ORDER_ID);
        assertThat(sub.getSellerId()).isEqualTo(SELLER_ID);
        assertThat(sub.getStoreName()).isEqualTo("Test Store");
        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.PENDING_PAYMENT);
        assertThat(sub.getItems()).hasSize(2);

        assertThat(sub.getSubtotal().amount()).isEqualByComparingTo("60.00");
        assertThat(sub.getShippingCost().amount()).isEqualByComparingTo("5.00");

        assertThat(sub.getTotal().amount()).isEqualByComparingTo("65.00");
        assertThat(sub.getCreatedAt()).isNotNull();
        assertThat(sub.getUpdatedAt()).isNotNull();
    }

    @Test
    void create_withZeroShipping_totalEqualsSubtotal() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("49.99"), BigDecimal.ZERO);

        assertThat(sub.getSubtotal().amount()).isEqualByComparingTo("49.99");
        assertThat(sub.getShippingCost().amount()).isEqualByComparingTo("0.00");
        assertThat(sub.getTotal().amount()).isEqualByComparingTo("49.99");
    }

    @Test
    void create_snapshotsItemPriceAtCheckoutTime() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("99.00"), BigDecimal.ZERO);

        assertThat(sub.getItems().get(0).getUnitPrice().amount())
                .isEqualByComparingTo("99.00");
    }

    @Test
    void statusTransition_fullHappyPath() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);

        sub.markPaid();
        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.PAID);

        sub.markProcessing();
        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.PROCESSING);

        sub.markShipped();
        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.SHIPPED);

        sub.markDelivered();
        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.DELIVERED);
    }

    @Test
    void cancel_whenPendingPayment_shouldSucceed() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);
        sub.cancel();
        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.CANCELLED);
    }

    @Test
    void cancel_whenPaid_shouldSucceed() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);
        sub.markPaid();
        sub.cancel();
        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.CANCELLED);
    }

    @Test
    void cancel_whenProcessing_shouldSucceed() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);
        sub.markPaid();
        sub.markProcessing();
        sub.cancel();
        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.CANCELLED);
    }

    @Test
    void cancel_whenShipped_shouldThrow() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);
        sub.markPaid();
        sub.markProcessing();
        sub.markShipped();

        assertThatThrownBy(sub::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("shipped");
    }

    @Test
    void cancel_whenDelivered_shouldThrow() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);
        sub.markPaid();
        sub.markProcessing();
        sub.markShipped();
        sub.markDelivered();

        assertThatThrownBy(sub::cancel)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void forceCancel_whenShipped_shouldCancelSuccessfully() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);
        sub.markPaid();
        sub.markProcessing();
        sub.markShipped();

        sub.forceCancel();

        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.CANCELLED);
    }

    @Test
    void forceCancel_whenPendingPayment_shouldCancel() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);
        sub.forceCancel();
        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.CANCELLED);
    }

    @Test
    void forceCancel_whenDelivered_shouldDoNothing() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);
        sub.markPaid();
        sub.markProcessing();
        sub.markShipped();
        sub.markDelivered();

        sub.forceCancel();

        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.DELIVERED);
    }

    @Test
    void forceCancel_whenAlreadyCancelled_shouldDoNothing() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);
        sub.cancel();

        sub.forceCancel();

        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.CANCELLED);
    }

    @Test
    void markRefunded_afterCancel_shouldSucceed() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);
        sub.cancel();
        sub.markRefunded();
        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.REFUNDED);
    }

    @Test
    void markProcessing_whenPendingPayment_shouldThrow() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);

        assertThatThrownBy(sub::markProcessing)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void markShipped_whenPaid_shouldThrow() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);
        sub.markPaid();

        assertThatThrownBy(sub::markShipped)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void markDelivered_whenShipped_thenMarkDeliveredAgain_shouldThrow() {
        SubOrder sub = buildSubOrder(1, new BigDecimal("20.00"), BigDecimal.ZERO);
        sub.markPaid();
        sub.markProcessing();
        sub.markShipped();
        sub.markDelivered();

        assertThatThrownBy(sub::markDelivered)
                .isInstanceOf(IllegalStateException.class);
    }

    private SubOrder buildSubOrder(int itemCount, BigDecimal unitPrice, BigDecimal shipping) {
        Cart cart = Cart.create(USER_ID);
        for (int i = 0; i < itemCount; i++) {
            cart.addItem(ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304" + i),
                    null, null, null, SELLER_ID, 1,
                    Money.of(unitPrice, "USD"), "Product " + i, "product-" + i, null);
        }
        return SubOrder.create(ORDER_ID, SELLER_ID, "Test Store",
                cart.getItems(), Money.of(shipping, "USD"), null);
    }
}
