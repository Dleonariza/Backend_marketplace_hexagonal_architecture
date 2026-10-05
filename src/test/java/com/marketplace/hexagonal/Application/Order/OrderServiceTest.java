package com.marketplace.hexagonal.Application.Order;

import com.marketplace.hexagonal.Application.Order.Services.OrderService;
import com.marketplace.hexagonal.Application.Order.UseCases.*;
import com.marketplace.hexagonal.Application.Order.UseCases.CancelOrderUseCaseImpl;
import com.marketplace.hexagonal.Application.Order.UseCases.SellerCancelOrderUseCaseImpl;
import com.marketplace.hexagonal.Application.Order.UseCases.AdminCancelOrderUseCaseImpl;
import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Order.Exceptions.EmptyCartException;
import com.marketplace.hexagonal.Domain.Order.Exceptions.OrderAccessDeniedException;
import com.marketplace.hexagonal.Domain.Order.Exceptions.OrderNotFoundException;
import com.marketplace.hexagonal.Domain.Order.Exceptions.SubOrderAccessDeniedException;
import com.marketplace.hexagonal.Domain.Order.Exceptions.SubOrderNotFoundException;
import com.marketplace.hexagonal.Domain.Order.Models.*;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderNumberGeneratorPort;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.SubOrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.Shipping.Ports.Out.SellerShippingOptionRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.PaymentMethodRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.marketplace.hexagonal.Domain.Order.Events.SubOrderStatusChangedEvent;
import com.marketplace.hexagonal.Domain.Order.Exceptions.OrderCancellationNotAllowedException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepositoryPort orderRepo;
    @Mock SubOrderRepositoryPort subOrderRepo;
    @Mock CartRepositoryPort cartRepo;
    @Mock SellerShippingOptionRepositoryPort shippingRepo;
    @Mock SellerProfileRepositoryPort sellerProfileRepo;
    @Mock OrderNumberGeneratorPort orderNumberGenerator;
    @Mock PaymentMethodRepositoryPort paymentMethodRepo;
    @Mock ApplicationEventPublisher eventPublisher;
    OrderService orderService;

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final UserId OTHER_USER = UserId.of("660e8400-e29b-41d4-a716-446655440001");
    private static final SellerProfileId SELLER_ID = SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002");
    private static final ProductId PRODUCT = ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final Money PRICE_50 = Money.of(new BigDecimal("50.00"), "USD");
    private static final Money ZERO = Money.of(BigDecimal.ZERO, "USD");
    private static final String IDEM_KEY = "550e8400-e29b-41d4-a716-111111111111";

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                new PlaceOrderUseCaseImpl(orderRepo, subOrderRepo, cartRepo,
                        shippingRepo, sellerProfileRepo, orderNumberGenerator, paymentMethodRepo),
                new GetOrderUseCaseImpl(orderRepo),
                new ListUserOrdersUseCaseImpl(orderRepo),
                new GetSellerSubOrdersUseCaseImpl(subOrderRepo),
                new UpdateSubOrderStatusUseCaseImpl(subOrderRepo),
                new CancelOrderUseCaseImpl(orderRepo),
                new SellerCancelOrderUseCaseImpl(orderRepo),
                new AdminCancelOrderUseCaseImpl(orderRepo),
                new ListAllOrdersUseCaseImpl(orderRepo),
                eventPublisher
        );
    }

    @Test
    void placeOrder_idempotencyKeyAlreadyUsed_returnsExistingOrder() {
        Order existing = buildOrder();
        when(orderRepo.findByUserIdAndIdempotencyKey(USER_ID, IDEM_KEY))
                .thenReturn(Optional.of(existing));

        Order result = orderService.placeOrder(USER_ID, IDEM_KEY, "addr-1", Map.of(), null);

        assertThat(result.getId()).isEqualTo(existing.getId());
        verify(orderRepo, never()).save(any());
        verify(cartRepo, never()).findActiveByUserId(any());
    }

    @Test
    void placeOrder_validCart_createsOrderAndChecksOutCart() {
        when(orderRepo.findByUserIdAndIdempotencyKey(any(), any())).thenReturn(Optional.empty());

        Cart cart = cartWithItem();
        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(sellerProfileRepo.findById(SELLER_ID)).thenReturn(Optional.empty());
        when(orderNumberGenerator.next()).thenReturn("O0000000001");
        when(orderRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(cartRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.placeOrder(USER_ID, IDEM_KEY, "addr-1", Map.of(), null);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);
        assertThat(result.getNumOrder()).isEqualTo("O0000000001");
        assertThat(result.getSubOrders()).hasSize(1);
        assertThat(result.getAllItems()).hasSize(1);
        assertThat(result.getSubtotal().amount()).isEqualByComparingTo("50.00");

        verify(orderRepo).save(any());
        verify(cartRepo).save(any());
    }

    @Test
    void placeOrder_emptyCart_throwsEmptyCartException() {
        when(orderRepo.findByUserIdAndIdempotencyKey(any(), any())).thenReturn(Optional.empty());
        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.of(Cart.create(USER_ID)));

        assertThatThrownBy(() -> orderService.placeOrder(USER_ID, IDEM_KEY, "addr-1", Map.of(), null))
                .isInstanceOf(EmptyCartException.class);
        verify(orderRepo, never()).save(any());
    }

    @Test
    void placeOrder_noActiveCart_throwsCartNotFoundException() {
        when(orderRepo.findByUserIdAndIdempotencyKey(any(), any())).thenReturn(Optional.empty());
        when(cartRepo.findActiveByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.placeOrder(USER_ID, IDEM_KEY, "addr-1", Map.of(), null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getOrder_ownerUser_returnsOrder() {
        Order saved = buildOrder();
        when(orderRepo.findById(any())).thenReturn(Optional.of(saved));

        Order result = orderService.getOrder(USER_ID, saved.getId().getValue());

        assertThat(result.getId()).isEqualTo(saved.getId());
    }

    @Test
    void getOrder_differentUser_throwsOrderAccessDeniedException() {
        Order saved = buildOrder();
        when(orderRepo.findById(any())).thenReturn(Optional.of(saved));

        assertThatThrownBy(() -> orderService.getOrder(OTHER_USER, saved.getId().getValue()))
                .isInstanceOf(OrderAccessDeniedException.class);
    }

    @Test
    void getOrder_notFound_throwsOrderNotFoundException() {
        when(orderRepo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(USER_ID, OrderId.generate().getValue()))
                .isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void listOrders_returnsAllUserOrders() {
        when(orderRepo.findByUserIdOrderByCreatedAtDesc(USER_ID))
                .thenReturn(List.of(buildOrder(), buildOrder()));

        List<Order> result = orderService.listOrders(USER_ID);

        assertThat(result).hasSize(2);
    }

    @Test
    void listOrders_noOrders_returnsEmptyList() {
        when(orderRepo.findByUserIdOrderByCreatedAtDesc(USER_ID)).thenReturn(List.of());

        assertThat(orderService.listOrders(USER_ID)).isEmpty();
    }

    @Test
    void getSellerSubOrders_returnsList() {
        SubOrder sub = buildSubOrder();
        when(subOrderRepo.findBySellerIdOrderByCreatedAtDesc(SELLER_ID))
                .thenReturn(List.of(sub));

        List<SubOrder> result = orderService.getSellerSubOrders(SELLER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSellerId()).isEqualTo(SELLER_ID);
    }

    @Test
    void updateSubOrderStatus_processing_succeeds() {
        SubOrder sub = buildSubOrder();
        sub.markPaid();
        when(subOrderRepo.findById(sub.getId())).thenReturn(Optional.of(sub));
        when(subOrderRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SubOrder result = orderService.updateSubOrderStatus(
                SELLER_ID, sub.getId().getValue(), SubOrderStatus.PROCESSING);

        assertThat(result.getStatus()).isEqualTo(SubOrderStatus.PROCESSING);
        verify(subOrderRepo).save(sub);

        verify(eventPublisher).publishEvent(any(SubOrderStatusChangedEvent.class));
    }

    @Test
    void updateSubOrderStatus_wrongSeller_throwsAccessDenied() {
        SubOrder sub = buildSubOrder();
        SellerProfileId other = SellerProfileId.of("999e8400-e29b-41d4-a716-446655440099");
        when(subOrderRepo.findById(sub.getId())).thenReturn(Optional.of(sub));

        assertThatThrownBy(() -> orderService.updateSubOrderStatus(
                other, sub.getId().getValue(), SubOrderStatus.PROCESSING))
                .isInstanceOf(SubOrderAccessDeniedException.class);

        verify(eventPublisher, never()).publishEvent(any(SubOrderStatusChangedEvent.class));
    }

    @Test
    void updateSubOrderStatus_notFound_throwsSubOrderNotFoundException() {
        when(subOrderRepo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateSubOrderStatus(
                SELLER_ID, SubOrderId.generate().getValue(), SubOrderStatus.PROCESSING))
                .isInstanceOf(SubOrderNotFoundException.class);

        verify(eventPublisher, never()).publishEvent(any(SubOrderStatusChangedEvent.class));
    }

    @Test
    void cancelOrder_paidOrder_cancelsSuccessfully() {
        Order order = buildOrderWithStatus(OrderStatus.PAID);
        when(orderRepo.findById(any())).thenReturn(Optional.of(order));
        when(orderRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.cancelOrder(USER_ID, order.getId().getValue());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepo).save(order);
    }

    @Test
    void cancelOrder_processingOrder_cancelsSuccessfully() {
        Order order = buildOrderWithStatus(OrderStatus.PROCESSING);
        when(orderRepo.findById(any())).thenReturn(Optional.of(order));
        when(orderRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.cancelOrder(USER_ID, order.getId().getValue());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelOrder_pendingPayment_cancelsSuccessfully() {
        Order order = buildOrder();
        when(orderRepo.findById(any())).thenReturn(Optional.of(order));
        when(orderRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.cancelOrder(USER_ID, order.getId().getValue());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelOrder_shippedOrder_throwsCancellationNotAllowed() {
        Order order = buildOrderWithStatus(OrderStatus.SHIPPED);
        when(orderRepo.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(USER_ID, order.getId().getValue()))
                .isInstanceOf(OrderCancellationNotAllowedException.class)
                .hasMessageContaining("SHIPPED");
    }

    @Test
    void cancelOrder_wrongUser_throwsAccessDenied() {
        Order order = buildOrder();
        when(orderRepo.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(OTHER_USER, order.getId().getValue()))
                .isInstanceOf(OrderAccessDeniedException.class);
        verify(orderRepo, never()).save(any());
    }

    @Test
    void cancelOrder_notFound_throwsOrderNotFoundException() {
        when(orderRepo.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(USER_ID, OrderId.generate().getValue()))
                .isInstanceOf(OrderNotFoundException.class);
    }

    private Cart cartWithItem() {
        Cart cart = Cart.create(USER_ID);
        cart.addItem(PRODUCT, null, null, null, SELLER_ID, 1,
                PRICE_50, "Product", "product", null);
        return cart;
    }

    private SubOrder buildSubOrder() {
        Cart cart = cartWithItem();
        OrderId orderId = OrderId.generate();
        return SubOrder.create(orderId, SELLER_ID, "Test Store",
                cart.getItems(), ZERO, null);
    }

    private Order buildOrder() {
        Cart cart = cartWithItem();
        OrderId orderId = OrderId.generate();
        SubOrder sub = SubOrder.create(orderId, SELLER_ID, "Test Store",
                cart.getItems(), ZERO, null);
        return Order.place(orderId, USER_ID, "O0000000001", IDEM_KEY,
                List.of(sub), ZERO, "addr-1", null, null);
    }

    @Test
    void sellerCancelOrder_shippedOrder_cancelsSuccessfully() {
        Order order = buildOrderWithStatus(OrderStatus.SHIPPED);
        when(orderRepo.findById(any())).thenReturn(Optional.of(order));
        when(orderRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.sellerCancelOrder(SELLER_ID, order.getId().getValue());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void sellerCancelOrder_deliveredOrder_throwsCancellationNotAllowed() {
        Order order = buildOrderWithStatus(OrderStatus.DELIVERED);
        when(orderRepo.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.sellerCancelOrder(SELLER_ID, order.getId().getValue()))
                .isInstanceOf(OrderCancellationNotAllowedException.class)
                .hasMessageContaining("DELIVERED");
    }

    @Test
    void sellerCancelOrder_wrongSeller_throwsAccessDenied() {
        Order order = buildOrderWithStatus(OrderStatus.PAID);
        SellerProfileId otherSeller = SellerProfileId.of("999e8400-e29b-41d4-a716-446655440099");
        when(orderRepo.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.sellerCancelOrder(otherSeller, order.getId().getValue()))
                .isInstanceOf(OrderAccessDeniedException.class);
        verify(orderRepo, never()).save(any());
    }

    @Test
    void adminCancelOrder_shippedOrder_cancelsSuccessfully() {
        Order order = buildOrderWithStatus(OrderStatus.SHIPPED);
        when(orderRepo.findById(any())).thenReturn(Optional.of(order));
        when(orderRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.adminCancelOrder(order.getId().getValue());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void adminCancelOrder_deliveredOrder_throwsCancellationNotAllowed() {
        Order order = buildOrderWithStatus(OrderStatus.DELIVERED);
        when(orderRepo.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.adminCancelOrder(order.getId().getValue()))
                .isInstanceOf(OrderCancellationNotAllowedException.class)
                .hasMessageContaining("DELIVERED");
    }

    @Test
    void adminCancelOrder_anyUserOrder_cancelsWithoutOwnershipCheck() {
        Order order = buildOrderWithStatus(OrderStatus.PROCESSING);
        when(orderRepo.findById(any())).thenReturn(Optional.of(order));
        when(orderRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.adminCancelOrder(order.getId().getValue());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    private Order buildOrderWithStatus(OrderStatus status) {
        Cart cart = cartWithItem();
        OrderId orderId = OrderId.generate();
        SubOrder sub = SubOrder.create(orderId, SELLER_ID, "Test Store",
                cart.getItems(), ZERO, null);
        return Order.reconstitute(
                orderId, USER_ID, "O0000000001", IDEM_KEY,
                List.of(sub), ZERO, ZERO, ZERO, ZERO,
                status, "addr-1", null, null,
                java.time.Instant.now(), java.time.Instant.now());
    }
}
