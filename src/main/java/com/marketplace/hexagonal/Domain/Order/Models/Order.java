package com.marketplace.hexagonal.Domain.Order.Models;

import com.marketplace.hexagonal.Domain.Order.Exceptions.EmptyCartException;
import com.marketplace.hexagonal.Domain.Order.Exceptions.OrderCancellationNotAllowedException;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Order {

    private OrderId id;
    private UserId userId;

    private String numOrder;

    private String idempotencyKey;

    private List<SubOrder> subOrders;

    private Money subtotal;

    private Money discount;

    private Money shippingCost;

    private Money total;

    private OrderStatus status;

    private String shippingAddressId;

    private String couponCode;

    private PaymentInfo paymentInfo;

    private Instant createdAt;
    private Instant updatedAt;

    public static Order place(
            OrderId orderId,
            UserId userId,
            String numOrder,
            String idempotencyKey,
            List<SubOrder> subOrders,
            Money discount,
            String shippingAddressId,
            String couponCode,
            PaymentInfo paymentInfo) {
        if (subOrders == null || subOrders.isEmpty())
            throw new EmptyCartException("Cannot place an order with an empty cart.");

        if (idempotencyKey == null || idempotencyKey.isBlank())
            throw new IllegalArgumentException("idempotencyKey must not be blank.");

        if (numOrder == null || numOrder.isBlank())
            throw new IllegalArgumentException("numOrder must not be blank.");

        String currency = subOrders.getFirst().getSubtotal().currency();

        Money subtotal = subOrders.stream()
                .map(SubOrder::getSubtotal)
                .reduce((a, b) -> Money.of(a.amount().add(b.amount()), a.currency()))
                .orElse(Money.of(BigDecimal.ZERO, currency));

        Money shippingCost = subOrders.stream()
                .map(SubOrder::getShippingCost)
                .reduce((a, b) -> Money.of(a.amount().add(b.amount()), a.currency()))
                .orElse(Money.of(BigDecimal.ZERO, currency));

        BigDecimal net = subtotal.amount()
                .subtract(discount.amount())
                .max(BigDecimal.ZERO)
                .add(shippingCost.amount());
        Money total = Money.of(net, currency);

        Instant now = Instant.now();
        return Order.builder()
                .id(orderId)
                .userId(userId)
                .numOrder(numOrder)
                .idempotencyKey(idempotencyKey)
                .subOrders(subOrders)
                .subtotal(subtotal)
                .discount(discount)
                .shippingCost(shippingCost)
                .total(total)
                .status(OrderStatus.PENDING_PAYMENT)
                .shippingAddressId(shippingAddressId)
                .couponCode(couponCode)
                .paymentInfo(paymentInfo)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static Order reconstitute(
            OrderId id,
            UserId userId,
            String numOrder,
            String idempotencyKey,
            List<SubOrder> subOrders,
            Money subtotal,
            Money discount,
            Money shippingCost,
            Money total,
            OrderStatus status,
            String shippingAddressId,
            String couponCode,
            PaymentInfo paymentInfo,
            Instant createdAt,
            Instant updatedAt) {
        Order o = new Order();
        o.id = id;
        o.userId = userId;
        o.numOrder = numOrder;
        o.idempotencyKey = idempotencyKey;
        o.subOrders = subOrders;
        o.subtotal = subtotal;
        o.discount = discount;
        o.shippingCost = shippingCost;
        o.total = total;
        o.status = status;
        o.shippingAddressId = shippingAddressId;
        o.couponCode = couponCode;
        o.paymentInfo = paymentInfo;
        o.createdAt = createdAt;
        o.updatedAt = updatedAt;
        return o;
    }

    public List<OrderItem> getAllItems() {
        return subOrders.stream()
                .flatMap(s -> s.getItems().stream())
                .toList();
    }

    public void markPaid() {
        requireStatus(OrderStatus.PENDING_PAYMENT);
        this.status = OrderStatus.PAID;
        this.updatedAt = Instant.now();

        subOrders.forEach(SubOrder::markPaid);
    }

    public void cancel() {
        if (status != OrderStatus.PENDING_PAYMENT
                && status != OrderStatus.PAID
                && status != OrderStatus.PROCESSING)
            throw new OrderCancellationNotAllowedException(status.name());
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
        subOrders.stream()
                .filter(s -> s.getStatus() != SubOrderStatus.SHIPPED
                          && s.getStatus() != SubOrderStatus.DELIVERED)
                .forEach(SubOrder::cancel);
    }

    public void cancelAsPrivileged() {
        if (status == OrderStatus.DELIVERED
                || status == OrderStatus.CANCELLED
                || status == OrderStatus.REFUNDED)
            throw new OrderCancellationNotAllowedException(status.name());
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
        subOrders.stream()
                .filter(s -> s.getStatus() != SubOrderStatus.DELIVERED)
                .forEach(SubOrder::forceCancel);
    }

    public void markRefunded() {
        requireStatus(OrderStatus.CANCELLED);
        this.status = OrderStatus.REFUNDED;
        this.updatedAt = Instant.now();
    }

    private void requireStatus(OrderStatus expected) {
        if (this.status != expected)
            throw new IllegalStateException(
                    "Expected order status " + expected + " but was " + this.status);
    }
}
