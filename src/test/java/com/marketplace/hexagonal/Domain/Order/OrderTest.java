package com.marketplace.hexagonal.Domain.Order;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Order.Exceptions.EmptyCartException;
import com.marketplace.hexagonal.Domain.Order.Exceptions.OrderCancellationNotAllowedException;
import com.marketplace.hexagonal.Domain.Order.Models.*;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final SellerProfileId SELLER_ID = SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002");
    private static final Money ZERO = Money.of(BigDecimal.ZERO, "USD");

    @Test
    void place_withValidSubOrders_shouldCreateOrderWithCorrectTotals() {
        OrderId orderId = OrderId.generate();
        SubOrder subOrder = buildSubOrder(orderId, 2, new BigDecimal("50.00"), BigDecimal.ZERO);

        Order order = Order.place(orderId, USER_ID, "O0000000001", "idem-key-1",
                List.of(subOrder), ZERO, "addr-1", null, null);

        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getUserId()).isEqualTo(USER_ID);
        assertThat(order.getIdempotencyKey()).isEqualTo("idem-key-1");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(order.getSubOrders()).hasSize(1);
        assertThat(order.getAllItems()).hasSize(2);
        assertThat(order.getSubtotal().amount()).isEqualByComparingTo("100.00");
        assertThat(order.getDiscount().amount()).isEqualByComparingTo("0.00");
        assertThat(order.getShippingCost().amount()).isEqualByComparingTo("0.00");
        assertThat(order.getTotal().amount()).isEqualByComparingTo("100.00");
        assertThat(order.getCreatedAt()).isNotNull();
    }

    @Test
    void place_withDiscountAndShipping_shouldComputeTotalCorrectly() {
        OrderId orderId = OrderId.generate();
        SubOrder subOrder = buildSubOrder(orderId, 1, new BigDecimal("100.00"), new BigDecimal("5.00"));
        Money discount = Money.of(new BigDecimal("10.00"), "USD");

        Order order = Order.place(orderId, USER_ID, "O0000000002", "idem-key-2",
                List.of(subOrder), discount, "addr-1", "SAVE10", null);

        assertThat(order.getTotal().amount()).isEqualByComparingTo("95.00");
        assertThat(order.getCouponCode()).isEqualTo("SAVE10");
    }

    @Test
    void place_withDiscountLargerThanSubtotal_totalShouldNotBeNegative() {
        OrderId orderId = OrderId.generate();
        SubOrder subOrder = buildSubOrder(orderId, 1, new BigDecimal("10.00"), new BigDecimal("5.00"));
        Money discount = Money.of(new BigDecimal("50.00"), "USD");

        Order order = Order.place(orderId, USER_ID, "O0000000003", "idem-key-3",
                List.of(subOrder), discount, "addr-1", null, null);

        assertThat(order.getTotal().amount()).isEqualByComparingTo("5.00");
    }

    @Test
    void place_withMultipleSellers_aggregatesTotalsCorrectly() {
        OrderId orderId = OrderId.generate();
        SellerProfileId seller2 = SellerProfileId.of("880e8400-e29b-41d4-a716-446655440003");

        SubOrder sub1 = buildSubOrder(orderId, 1, new BigDecimal("30.00"), new BigDecimal("5.00"));
        SubOrder sub2 = SubOrder.create(orderId, seller2, "Store B",
                cartItems(seller2, 2, new BigDecimal("20.00")),
                Money.of(new BigDecimal("3.00"), "USD"), null);

        Order order = Order.place(orderId, USER_ID, "O0000000004", "idem-key-4",
                List.of(sub1, sub2), ZERO, "addr-1", null, null);

        assertThat(order.getSubOrders()).hasSize(2);
        assertThat(order.getSubtotal().amount()).isEqualByComparingTo("70.00");
        assertThat(order.getShippingCost().amount()).isEqualByComparingTo("8.00");
        assertThat(order.getTotal().amount()).isEqualByComparingTo("78.00");
    }

    @Test
    void place_withEmptySubOrders_shouldThrowEmptyCartException() {
        assertThatThrownBy(() -> Order.place(OrderId.generate(), USER_ID, "O0000000005", "idem-key-5",
                List.of(), ZERO, "addr-1", null, null))
                .isInstanceOf(EmptyCartException.class);
    }

    @Test
    void place_withNullSubOrders_shouldThrowEmptyCartException() {
        assertThatThrownBy(() -> Order.place(OrderId.generate(), USER_ID, "O0000000006", "idem-key-6",
                null, ZERO, "addr-1", null, null))
                .isInstanceOf(EmptyCartException.class);
    }

    @Test
    void place_withBlankIdempotencyKey_shouldThrow() {
        OrderId orderId = OrderId.generate();
        SubOrder subOrder = buildSubOrder(orderId, 1, new BigDecimal("10.00"), BigDecimal.ZERO);
        assertThatThrownBy(() -> Order.place(orderId, USER_ID, "O0000000007", "  ",
                List.of(subOrder), ZERO, "addr-1", null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void markPaid_propagatesToAllSubOrders() {
        Order order = placedOrder();
        order.markPaid();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        order.getSubOrders().forEach(s ->
                assertThat(s.getStatus()).isEqualTo(SubOrderStatus.PAID));
    }

    @Test
    void cancel_whenPendingPayment_shouldSucceed() {
        Order order = placedOrder();
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancel_whenPaid_shouldSucceed() {
        Order order = orderWithStatus(OrderStatus.PAID);
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancel_whenProcessing_shouldSucceed() {
        Order order = orderWithStatus(OrderStatus.PROCESSING);
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancel_whenShipped_shouldThrow() {
        Order order = orderWithStatus(OrderStatus.SHIPPED);
        assertThatThrownBy(order::cancel)
                .isInstanceOf(OrderCancellationNotAllowedException.class)
                .hasMessageContaining("SHIPPED");
    }

    @Test
    void cancel_whenDelivered_shouldThrow() {
        Order order = orderWithStatus(OrderStatus.DELIVERED);
        assertThatThrownBy(order::cancel)
                .isInstanceOf(OrderCancellationNotAllowedException.class)
                .hasMessageContaining("DELIVERED");
    }

    @Test
    void cancel_whenAlreadyCancelled_shouldThrow() {
        Order order = orderWithStatus(OrderStatus.CANCELLED);
        assertThatThrownBy(order::cancel)
                .isInstanceOf(OrderCancellationNotAllowedException.class);
    }

    @Test
    void cancelAsPrivileged_whenShipped_shouldSucceed() {
        Order order = orderWithStatus(OrderStatus.SHIPPED);
        order.cancelAsPrivileged();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelAsPrivileged_whenPendingPayment_shouldSucceed() {
        Order order = placedOrder();
        order.cancelAsPrivileged();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelAsPrivileged_whenDelivered_shouldThrow() {
        Order order = orderWithStatus(OrderStatus.DELIVERED);
        assertThatThrownBy(order::cancelAsPrivileged)
                .isInstanceOf(OrderCancellationNotAllowedException.class)
                .hasMessageContaining("DELIVERED");
    }

    @Test
    void cancelAsPrivileged_whenAlreadyCancelled_shouldThrow() {
        Order order = orderWithStatus(OrderStatus.CANCELLED);
        assertThatThrownBy(order::cancelAsPrivileged)
                .isInstanceOf(OrderCancellationNotAllowedException.class);
    }

    @Test
    void cancelAsPrivileged_forcesCancelOnShippedSubOrders() {
        OrderId orderId = OrderId.generate();
        SubOrder sub = buildSubOrder(orderId, 1, new BigDecimal("50.00"), BigDecimal.ZERO);

        sub.markPaid();
        sub.markProcessing();
        sub.markShipped();

        Order order = orderWithStatusAndSubOrders(OrderStatus.SHIPPED, orderId, List.of(sub));
        order.cancelAsPrivileged();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(sub.getStatus()).isEqualTo(SubOrderStatus.CANCELLED);
    }

    @Test
    void markPaid_whenAlreadyPaid_shouldThrow() {
        Order order = placedOrder();
        order.markPaid();
        assertThatThrownBy(order::markPaid)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void refund_afterCancel_shouldSucceed() {
        Order order = placedOrder();
        order.cancel();
        order.markRefunded();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.REFUNDED);
    }

    @Test
    void getAllItems_returnsAllItemsAcrossSubOrders() {
        OrderId orderId = OrderId.generate();
        SellerProfileId seller2 = SellerProfileId.of("880e8400-e29b-41d4-a716-446655440003");

        SubOrder sub1 = buildSubOrder(orderId, 2, new BigDecimal("10.00"), BigDecimal.ZERO);
        SubOrder sub2 = SubOrder.create(orderId, seller2, "Store B",
                cartItems(seller2, 3, new BigDecimal("10.00")),
                ZERO, null);

        Order order = Order.place(orderId, USER_ID, "O0000000008", "idem-key-all",
                List.of(sub1, sub2), ZERO, "addr-1", null, null);

        assertThat(order.getAllItems()).hasSize(5);
    }

    private java.util.List<com.marketplace.hexagonal.Domain.Cart.Models.CartItem>
    cartItems(SellerProfileId sellerId, int count, BigDecimal unitPrice) {
        Cart cart = Cart.create(USER_ID);
        for (int i = 0; i < count; i++) {
            cart.addItem(ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304" + i),
                    null, null, null, sellerId, 1,
                    Money.of(unitPrice, "USD"), "Product " + i, "product-" + i, null);
        }
        return cart.getItems();
    }

    private SubOrder buildSubOrder(OrderId orderId, int itemCount,
                                   BigDecimal unitPrice, BigDecimal shippingAmount) {
        return SubOrder.create(orderId, SELLER_ID, "Test Store",
                cartItems(SELLER_ID, itemCount, unitPrice),
                Money.of(shippingAmount, "USD"), null);
    }

    private Order placedOrder() {
        OrderId orderId = OrderId.generate();
        SubOrder subOrder = buildSubOrder(orderId, 1, new BigDecimal("50.00"), BigDecimal.ZERO);
        return Order.place(orderId, USER_ID, "O0000000009", "idem-key-test",
                List.of(subOrder), ZERO, "addr-1", null, null);
    }

    private Order orderWithStatus(OrderStatus status) {
        OrderId orderId = OrderId.generate();
        SubOrder sub = buildSubOrder(orderId, 1, new BigDecimal("50.00"), BigDecimal.ZERO);
        return Order.reconstitute(orderId, USER_ID, "O0000000010", "idem-key-status",
                List.of(sub), ZERO, ZERO, ZERO, ZERO, status, "addr-1", null, null, null, null);
    }

    private Order orderWithStatusAndSubOrders(OrderStatus status, OrderId orderId,
                                              List<SubOrder> subOrders) {
        return Order.reconstitute(orderId, USER_ID, "O0000000011", "idem-key-privileged",
                subOrders, ZERO, ZERO, ZERO, ZERO, status, "addr-1", null, null, null, null);
    }
}
