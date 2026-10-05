package com.marketplace.hexagonal.Application.Order.Services;

import com.marketplace.hexagonal.Domain.Order.Events.OrderCancelledEvent;
import com.marketplace.hexagonal.Domain.Order.Events.OrderPlacedEvent;
import com.marketplace.hexagonal.Domain.Order.Events.SubOrderStatusChangedEvent;
import com.marketplace.hexagonal.Domain.Order.Models.*;
import com.marketplace.hexagonal.Domain.Order.Ports.In.*;
import com.marketplace.hexagonal.Domain.Product.Models.PageResult;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class OrderService {

    private final PlaceOrderUseCase placeOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final ListUserOrdersUseCase listUserOrdersUseCase;
    private final GetSellerSubOrdersUseCase getSellerSubOrdersUseCase;
    private final UpdateSubOrderStatusUseCase updateSubOrderStatusUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final SellerCancelOrderUseCase sellerCancelOrderUseCase;
    private final AdminCancelOrderUseCase adminCancelOrderUseCase;
    private final ListAllOrdersUseCase listAllOrdersUseCase;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Order placeOrder(UserId userId,
                            String idempotencyKey,
                            String shippingAddressId,
                            Map<String, String> shippingSelections,
                            String paymentMethodId) {
        Order order = placeOrderUseCase.execute(userId, idempotencyKey,
                shippingAddressId, shippingSelections, paymentMethodId);

        eventPublisher.publishEvent(new OrderPlacedEvent(order.getId(), order.getUserId()));
        return order;
    }

    @Transactional(readOnly = true)
    public Order getOrder(UserId userId, String orderId) {
        return getOrderUseCase.execute(userId, orderId);
    }

    @Transactional(readOnly = true)
    public List<Order> listOrders(UserId userId) {
        return listUserOrdersUseCase.execute(userId);
    }

    @Transactional
    public Order cancelOrder(UserId userId, String orderId) {
        Order cancelled = cancelOrderUseCase.execute(userId, orderId);
        eventPublisher.publishEvent(new OrderCancelledEvent(
                cancelled.getId(), cancelled.getNumOrder(),
                cancelled.getUserId(), "BUYER",
                java.time.Instant.now()));
        return cancelled;
    }

    @Transactional
    public Order sellerCancelOrder(SellerProfileId sellerId, String orderId) {
        Order cancelled = sellerCancelOrderUseCase.execute(sellerId, orderId);
        eventPublisher.publishEvent(new OrderCancelledEvent(
                cancelled.getId(), cancelled.getNumOrder(),
                cancelled.getUserId(), "SELLER",
                java.time.Instant.now()));
        return cancelled;
    }

    @Transactional
    public Order adminCancelOrder(String orderId) {
        Order cancelled = adminCancelOrderUseCase.execute(orderId);
        eventPublisher.publishEvent(new OrderCancelledEvent(
                cancelled.getId(), cancelled.getNumOrder(),
                cancelled.getUserId(), "ADMIN",
                java.time.Instant.now()));
        return cancelled;
    }

    @Transactional(readOnly = true)
    public PageResult<Order> listAllOrders(AdminOrderSearchCriteria criteria) {
        return listAllOrdersUseCase.listAll(criteria);
    }

    @Transactional(readOnly = true)
    public List<SubOrder> getSellerSubOrders(SellerProfileId sellerId) {
        return getSellerSubOrdersUseCase.execute(sellerId);
    }

    @Transactional
    public SubOrder updateSubOrderStatus(SellerProfileId sellerId,
                                         String subOrderId,
                                         SubOrderStatus newStatus) {
        SubOrder updated = updateSubOrderStatusUseCase.execute(sellerId, subOrderId, newStatus);

        eventPublisher.publishEvent(new SubOrderStatusChangedEvent(
                updated.getId(),
                updated.getOrderId(),
                updated.getStatus(),
                updated.getStoreName(),
                updated.getUpdatedAt()
        ));
        return updated;
    }
}
